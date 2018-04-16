#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<string.h>
#include <jni.h>

#define LOG_TAG "air-codec"

#ifdef BUILD_FROM_SOURCE
#include <utils/Log.h>
#else
#include <android/log.h>
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)

#endif

#define JNI_COPY    0

//==========================================
jint Java_com_infraredctrl_aircodec_AirCodec_getcodec(JNIEnv* env,
		jobject obj, jbyteArray srcArray[], jint length,
		jintArray paramentArray[], jbyteArray dataArray[]) {
	jsize samples_sz, parament_sz, data_sz;
	jbyte *samples, *data;
	jint *parament;
	int status = 0;

	//	LOGD("getcodec(%p, %d, %p, %p)",
	//			srcArray, length, paramentArray,
	//			dataArray);

	samples_sz = (*env)->GetArrayLength(env, srcArray);
	samples = (*env)->GetByteArrayElements(env, srcArray, JNI_COPY);
	parament_sz = (*env)->GetArrayLength(env, paramentArray);
	parament = (*env)->GetIntArrayElements(env, paramentArray, JNI_COPY);
	data_sz = (*env)->GetArrayLength(env, dataArray);
	data = (*env)->GetByteArrayElements(env, dataArray, JNI_COPY);

	LOGD("len%d",length);
	LOGD("%d",samples_sz);
	// Revert buffer pointers
	//	samples -= sampleLength;
	//	data -= bytes_encoded;

	//--------------------
	//	LOGD("samples:%x",
	//			samples[0]);
	//	LOGD("parament:%x",
	//			parament[0]);
	//--------------------

	status = decode(samples, length, parament, data);
	(*env)->ReleaseByteArrayElements(env, srcArray, samples, JNI_COPY);
	(*env)->ReleaseByteArrayElements(env, dataArray, data, JNI_COPY);

	return status;
}

//==========================================================================================
void Java_com_infraredctrl_aircodec_AirCodec_getContentData(JNIEnv* env,jobject obj,jbyteArray  data[], jint length, jbyteArray level[]){
		jsize dataSize,backDataSize;
		jbyte *contentdata, *backData;

		dataSize = (*env)->GetArrayLength(env, data);
		contentdata = (*env)->GetByteArrayElements(env, data, JNI_COPY);
		backDataSize = (*env)->GetArrayLength(env, level);
		backData = (*env)->GetIntArrayElements(env, level, JNI_COPY);

		  generate_level_data(contentdata, length,backData);
		(*env)->ReleaseByteArrayElements(env, data, contentdata, JNI_COPY);
		(*env)->ReleaseByteArrayElements(env, level, backData, JNI_COPY);

}
jint Java_com_infraredctrl_aircodec_AirCodec_getheadSize(JNIEnv* env,jobject obj,jbyteArray  data[], jint length){
		jsize dataSize;
		jbyte *contentdata;
		jint backLen;
		dataSize = (*env)->GetArrayLength(env, data);
		contentdata = (*env)->GetByteArrayElements(env, data, JNI_COPY);
		backLen=generate_head_size(contentdata,length);
		(*env)->ReleaseByteArrayElements(env, data, contentdata, JNI_COPY);
		return backLen;
}

jint Java_com_infraredctrl_aircodec_AirCodec_levelCompare(JNIEnv* env,jobject obj,jbyteArray  data1[],jbyteArray  data2[],jint length){
		jsize data1Size,data2Size;
		jbyte *comdata1,*comdata2;
		jint backLen;

		data1Size = (*env)->GetArrayLength(env, data1);
		comdata1 = (*env)->GetByteArrayElements(env, data1, JNI_COPY);
		data2Size = (*env)->GetArrayLength(env, data2);
		comdata2 = (*env)->GetByteArrayElements(env, data2, JNI_COPY);

		backLen=level_compare(comdata1,comdata2,length);

		(*env)->ReleaseByteArrayElements(env, data1, comdata1, JNI_COPY);
		(*env)->ReleaseByteArrayElements(env, data2, comdata2, JNI_COPY);
		return backLen;
}
//==========================================


typedef struct {
	int cod;
	int sw;
	int tem;
	int wd;
	int wv;
} AC_InitTypeDef;

typedef struct {
	int high_level[4];
	int low_level[4];
} Level_InitTypeDef;

typedef struct {
	int *x;
	int select;
	Level_InitTypeDef Level_Struct;
	int *position;
	int *initdata;
	int data_size[2];
} ACcfg_InitTypeDef;

/**
 * @brief  TWO CHAR DATA TRANSFORMAT TO INT DATA
 * @param  x is initdata
 *			y is after changed data that size is half of initdata's size
 *			size is initdata size
 * @retval None
 */
int *rem2bit(int *x, int size) {
	int i;
	int *temp;
	int *y;

	/*initial data*/
	temp = (int *) calloc(size, sizeof(int));
	y = (int *) calloc(size / 2, sizeof(int));

	/*combination and assignment*/
	for (i = 0; i < size; i++, i++) {
		if ((x[i] / 128) >= 1) {
			temp[i] = x[i] - 128;
		}
		temp[i] = temp[i] * 256 + x[i + 1];
	}
	for (i = 0; i < size / 2; i++) {
		y[i] = temp[i * 2];
	}
	free(temp);

	return (y);
}

/**
 * @brief  find data head size
 * @param  x is after chenge data
 * @retval head is header size
 */
int judge_head(int *x) {
	int i;
	int data[10] = { 0 };
	float load = 0;
	int head_size = 0;

	for (i = 0; i < 10; i++) {
		data[i] = x[i];
	}
	//caculate one bit size
	for (i = 0; i < 5; i++) {
		data[i] = data[i] + data[i + 1];
	}
	for (i = 0; i < 4; i++) {
		load = (float) data[i] / (float) data[0];
		if (load > 0.9) {
			head_size++;
		}
	}
	if (head_size >= 5) {
		head_size = 1;
	}
	return (head_size);
}

/**
 * @brief  TAKE EVEN BIT
 before use this function need initial array
 for example:
 //initial array
 y = (int *)calloc(size/2,sizeof(int));
 * @param  x IS AFTER CHANGE DATA
 y IS EVEN BIT DATA
 * @retval none
 */

static void take_even(int *x, int *y, int size) {
	int i = 0;
	/**/
	for (i = 0; i < size / 2; i++) {
		y[i] = x[2 * i + 1];
	}
}
/**
 * @brief  SORT DATA
 * @param  x IS AFTER CHANGE DATA
 * @retval y IS HEAD SIZE
 */
static void sort(int *data) {
	int temp = 0;
	int i;
	int j;

	/*Bubble sort method*/
	for (i = 0; i < 10; i++) {
		for (j = i + 1; j < 10; j++) {
			if (data[i] > data[j]) {
				temp = data[i];
				data[i] = data[j];
				data[j] = temp;
			}
		}
	}
}
/**
 * @brief  SORT DATA
 * @param  x IS AFTER CHANGE DATA
 * @retval y IS HEAD SIZE
 */
int max_10bit(int *x) {
	int i;
	int data[10] = { 0 };
	/*take 10 bit data*/
	for (i = 0; i < 10; i++)

	{
		data[i] = x[i];
	}

	sort(data);
	return (data[9]);
}

/**
 * @brief  SORT DATA
 * @param  x IS AFTER CHANGE DATA
 * @retval y IS HEAD SIZE
 */
int min_10bit(int *x) {
	int i;
	int data[10] = { 0 };
	/*take 10 bit data*/
	for (i = 0; i < 10; i++)

	{
		data[i] = x[i];
	}

	sort(data);
	return (data[0]);

}
/**
 * @brief  TWO CHAR DATA TRANSFORMAT
 before use this function need initial array
 for example:
 //initial array
 y = (int *)calloc(size,sizeof(int));

 * @param  x is after chenge data
 mode is select mode 0. <;1. =;2. >;3. <=;4.>=
 size is x data length
 y is location that is after compared data in specified mode
 * @retval j is y real size
 */
int find_values(int *x, int *y, int mode, int value, int size) {
	int i;
	int j = 0;

	/*select less than value data*/
	if (mode == 0) {
		for (i = 0; i < size; i++) {
			if (x[i] < value) {
				y[j] = i;
				j++;
			}
		}
	}
	/*select equal value data*/
	else if (mode == 1) {
		for (i = 0; i < size; i++) {
			if (x[i] == value) {
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than value data*/
	else if (mode == 2) {
		for (i = 0; i < size; i++) {
			if (x[i] > value) {
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than or equal value data*/
	else if (mode == 3) {
		for (i = 0; i < size; i++) {
			if (x[i] <= value) {
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than or equal value data*/
	else if (mode == 4) {
		for (i = 0; i < size; i++) {
			if (x[i] >= value) {
				y[j] = i;
				j++;
			}
		}
	}
	return (j);
}

/**
 * @brief  REPLACE DATA
 * @param  x IS
 *			y is
 */
void replace_data(int *x, int *adr, int size, int data) {
	int i = 0;
	for (i = 0; i < size; i++) {
		x[adr[i]] = data;
	}
}

/**
 * @brief  TWO CHAR DATA TRANSFORMAT
 * @param  x is after chenge data
 * @retval y is head size
 */
int *low_level_modulation(int *x, int maxdata, int size) {
	int i = 0;
	int *adr = 0;
	int *y = 0;
	int count = 0;
	int min = 0;
	int max = 0;
	int mid = 0;

	/*initial x array*/
	y = (int *) calloc(size / 2, sizeof(int));
	take_even(x, y, size);
	min = min_10bit(y);

	//replace special data
	adr = (int *) calloc(size, sizeof(int));
	maxdata = maxdata * 9 / 10;
	size = size / 2;
	count = find_values(y, adr, 4, maxdata, size);

	replace_data(y, adr, count, min);
	max = max_10bit(y);

	//modulation
	mid = (max + min) / 2;
	for (i = 0; i < size; i++) {
		if (y[i] > mid) {
			y[i] = 1;
		} else {
			y[i] = 0;
		}
	}
	return (y);

}
/**
 * @brief  ��ȡһ����ݣ���first~end
 * @param  x is after chenge data
 * @retval y is head size
 */
int *take_data(int *x, int first, int end) {
	int i = 0;
	int j = 0;
	int *p = 0;
	int length = 0;

	length = end - first + 1;
	p = (int *) calloc(length, sizeof(int));
	for (i = first; i <= end; i++) {
		p[j] = x[i];
		j++;
	}
	return (p);
}

/**
 * @brief  ��ԭ����0��1��ݷ����ԭʼ��ݵ���ʽ��
 * @param  imfo
 Level_Struct
 size
 * @retval data
 */
int *imfo2data(int *imfo, Level_InitTypeDef Level_Struct, int size) {
	int i;
	int j;
	int *data;

	data = (int *) calloc(size * 4, sizeof(int));//atctually set the size of four
	for (i = 0; i < size; i++) {
		if (imfo[i] == 0) {
			for (j = 0; j < 4; j++) {
				data[i * 4 + j] = Level_Struct.low_level[j];
			}
		} else {
			for (j = 0; j < 4; j++) {
				data[i * 4 + j] = Level_Struct.high_level[j];
			}
		}
	}
	return (data);

}

/**
 * @brief
 * @param  ������Ϣ�������滻
 ACcfg_InitTypeDef�����У�select�н���ѡ����ݣ�
 initdataΪѡ��״̬����Ϣλ
 positionΪ��Ҫ�滻��λ��
 sizeΪposition�Ĵ�С
 * @retval data
 */
void AC_set_status(ACcfg_InitTypeDef ACcfg_Struct) {
	int i, j;
	int *replace_data = 0;
	int *imfo = 0;
	int m, n;
	int select = 0;

	select = ACcfg_Struct.select;
	m = ACcfg_Struct.data_size[0];
	n = ACcfg_Struct.data_size[1];

	/*take need data*/
	imfo = (int *) calloc(n, sizeof(int));
	select = select * n;
	/**/
	replace_data = (int *) calloc(4 * n, sizeof(int));
	for (i = 0; i < n; i++) {
		imfo[i] = ACcfg_Struct.initdata[i + select];
	}
	replace_data = imfo2data(imfo, ACcfg_Struct.Level_Struct, n);
	free(imfo);
	/**/
	for (i = 0; i < n; i++) {
		ACcfg_Struct.position[i] = ACcfg_Struct.position[i] * 4;
	}
	for (i = 0; i < n; i++) {
		for (j = 0; j < 4; j++) {
			ACcfg_Struct.x[ACcfg_Struct.position[i] + j] = replace_data[i * 4
					+ j];
		}
	}

}

//�ж�addrλΪβ
static int judge_tail(int addr, short int str[]) {
	int count = 0;
	int i = 0;
	unsigned char tail = 0xff; //�̶���β

	for (i = 0; i < 4; i++) {
		if (str[addr] == tail) {
			count = count + 1;
			addr = addr + 1;
		}
	}
	if (count != 4) {
		return (0);
	}
	return (1);
}

int *get_address(short int str[], int length) {
	int i = 0;
	int j = 0;
	int group[18] = { 0 };//A set of three data, the first is length, the second is interval, the starting address is the third
	int *p = 0;
	int temp = 0;
	int count = 0;

	p = (int *) calloc(18, sizeof(int));
	length -= 3; //���������β�κ�����
	for (i = 0; i < 3; i++) {
		if (str[i] == 0xaa) {
			j++;
		}
	}
	/*ͷ������ȷ����ʽ�½���ȡֵ*/
	if (j == 3) {
		while (i < length) {
			if (i > length) {
				return (p);
			}
			switch (str[i]) {
			case 'o': //o = 0x6f; g = 0x67
			{
				i++;
				if (str[i] == 'g') {
					i++;
					group[0] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[1] = (int) str[i];//interval
					i = i + 1;
					group[2] = i;//address
					temp = group[2] + group[0];

					if (judge_tail(temp, str) == 0) {
						group[2] = 0;
					}
				}
				i = temp + 4; //���4��β����i������һ��ͷ�Ĵ�
				break;
			}
			case 't': {
				i++;
				if (str[i] == 'e') {
					i++;
					group[3] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[4] = (int) str[i];//interval
					i = i + 1;
					group[5] = i;//address
					temp = group[5] + group[3];

					if (judge_tail(temp, str) == 0) {
						group[5] = 0;
					}
				}
				i = temp + 4; //���4��β����i������һ��ͷ�Ĵ�
				break;
			}
			case 's': {
				i++;
				if (str[i] == 'w') {
					i++;
					group[6] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[7] = (int) str[i];//interval
					i = i + 1;
					group[8] = i;//address

					temp = group[8] + group[6];

					if (judge_tail(temp, str) == 0) {
						group[8] = 0;
					}
				}
				i = temp + 4; //���4��β����i������һ��ͷ�Ĵ�
				count = 0;
				break;
			}
			case 'c': {
				i++;
				if (str[i] == 'd') {
					i++;
					group[9] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[10] = (int) str[i];//interval
					i = i + 1;
					group[11] = i;//address

					temp = group[11] + group[9];

					if (judge_tail(temp, str) == 0) {
						group[11] = 0;
					}
				}
				i = temp + 4; //��i������һ��ͷ�Ĵ�
				count = 0;
				break;
			}
			case 'w': {
				i++;
				if (str[i] == 'v') {
					i++;
					group[12] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[13] = (int) str[i];//interval
					i = i + 1;
					group[14] = i;//address
					temp = group[14] + group[12];

					if (judge_tail(temp, str) == 0) {
						group[14] = 0;
					}
				} else if (str[i] == 'd') {
					i++;
					group[15] = (int) str[i] * 128 + (int) str[i + 1];//length
					i = i + 2;
					group[16] = (int) str[i];//interval
					i = i + 1;
					group[17] = i;//address
					temp = group[17] + group[15];

					if (judge_tail(temp, str) == 0) {
						group[17] = 0;
					}
				}
				i = temp + 4; //���4��β����i������һ��ͷ�Ĵ�
				break;
			}
			default:
				return (p); //�������쳣�Ǽ�ʱ�����
			}
		}
		for (i = 0; i < 18; i++) {
			p[i] = group[i];
		}

		return (p); //��ʾ��ȷ��
	} else {
		return (p);
	}
}

/**
 * @brief  CONDITION SET
 * @param  x is after chenge data
 * @retval y is head size
 */

static void para_set(ACcfg_InitTypeDef ACcfg_Struct, int AC_para, int para[]) {
	int length = para[0] * 128 + para[1];
	int interval = para[2];
	int *position;
	int *initdata;
	int i;
	int temp = 3;

	length = length - interval;
	/*initial position*/
	position = (int *) calloc(4, sizeof(int));
	for (i = 0; i < interval; i++) {
		position[i] = para[temp + i];
	}
	/*initial wind voloaty data*/
	temp = temp + interval;
	initdata = (int *) calloc(length, sizeof(int));
	for (i = 0; i < length; i++) {
		initdata[i] = para[temp + i];
	}
	ACcfg_Struct.data_size[0] = length; //	m
	ACcfg_Struct.data_size[1] = interval; //	n

	ACcfg_Struct.position = position;
	ACcfg_Struct.initdata = initdata;
	ACcfg_Struct.select = AC_para;
	AC_set_status(ACcfg_Struct);

	/*free RAM*/
	free(position);
	free(initdata);
}

/**
 * @brief  get level data
 * @param  original data
 * @retval y is the level data
 */
void get_level_data(int og_data[], int length, int level[8]) {
	int i = 0;
	int head = 0;
	int maxdata = 0;
	int *y;
	int *temp;
	int tmp = 0;
	int *x;
	x = (int *) calloc(length, sizeof(int));
	for (i = 0; i < length; i++) {
		x[i] = og_data[i];
	}
	/*initial data*/
	y = (int *) calloc(length / 2, sizeof(int));
	temp = (int *) calloc(length / 2 - 2, sizeof(int));

	y = rem2bit(x, length);
	head = judge_head(y);
	maxdata = y[1];

	head = head * 2;
	length = length / 2 - head - 1;
	//data from y[head*2]~y[end-1]
	for (i = 0; i < length; i++) {
		temp[i] = y[i + head];
	}
	free(y);
	y = (int *) calloc(length, sizeof(int));
	y = low_level_modulation(temp, maxdata, length);
	free(temp);
	length = length / 2;
	temp = (int *) calloc(length, sizeof(int));

	/*get low level*/
	tmp = find_values(y, temp, 1, 0, length);
	if (tmp > 0) {
		tmp = temp[0] * 4;
		tmp = tmp + head * 2;
		for (i = 0; i < 4; i++) {
			level[i] = x[tmp];
			tmp++;
		}
	}
	free(temp);
	/*get high level*/
	temp = (int *) calloc(length, sizeof(int));
	tmp = find_values(y, temp, 1, 1, length);
	if (tmp > 0) {
		tmp = temp[0] * 4;
		tmp = tmp + head * 2;
		for (i = 0; i < 4; i++) {
			level[4 + i] = x[tmp];
			tmp++;
		}
	}

	free(x);
	free(y);
	free(temp);
}
/**
 * @brief  test data
 * @param  parament is AC_data

 * @retval test 0 is false; 1 is right
 */
int test_data(int parament[]) {
	int str[] = { 15, 1, 4, 3, 1 };
	int i = 0;
	for (i = 0; i < 5; i++) {
		if (str[i] < parament[i]) {
			return (0);
		}
	}
	return (1);
}
/**
 * @brief  get data
 * @param  str is packet
 length is packet length
 * @retval
 */
int decode(short int str[], int length, int parament[6], short int cdata[]) {
	int i = 0;
	int *og_data;
	int *cd_data;
	int *sw_data;
	int *te_data;
	int *wd_data;
	int *wv_data;
	int *group;
	int temp = 0;
	int addr = 0;
	int level[8] = { 0 };

	Level_InitTypeDef Level_Struct;
	ACcfg_InitTypeDef ACcfg_Struct;

	if (test_data(parament) == 0) {
		return (0);
	}

	group = (int *) calloc(18, sizeof(int));
	group = get_address(str, length);

	og_data = (int *) calloc(group[0] + 3, sizeof(int));
	te_data = (int *) calloc(group[3] + 3, sizeof(int));
	sw_data = (int *) calloc(group[6] + 3, sizeof(int));
	cd_data = (int *) calloc(group[9] + 3, sizeof(int));
	wv_data = (int *) calloc(group[12] + 3, sizeof(int));
	wd_data = (int *) calloc(group[15] + 3, sizeof(int));
	/*judge data is right*/
	for (i = 0; i < 6; i++) {
		if (group[2 + i * 3] == 0) {
			/*free all data*/
			free(og_data);
			free(cd_data);
			free(sw_data);
			free(te_data);
			free(wd_data);
			free(wv_data);
			free(group);
			return (0);
		}
		temp = 2 + i * 3;
	}

	/*take data*/
	temp = group[0]; //the first data
	for (i = 0; i < temp; i++) {
		og_data[i] = (int) str[group[2] + i];
	}
	get_level_data(og_data, temp, level); //��ȡ�ߵ͵�ƽ��Ϣ

	for (i = 0; i < 4; i++) {
		Level_Struct.low_level[i] = level[i];
	}
	for (i = 0; i < 4; i++) {
		Level_Struct.high_level[i] = level[4 + i];
	}

	ACcfg_Struct.x = og_data;
	ACcfg_Struct.Level_Struct = Level_Struct; //ACcfg��ʼ������
	//	free(tmp);
	temp = group[3] + 3; //the second data length
	addr = group[5] - 3;
	for (i = 0; i < temp; i++) {
		te_data[i] = str[addr + i];
	}

	para_set(ACcfg_Struct, parament[0], te_data); //��ʱ����������Ĳ���

	temp = group[6] + 3; //the third data length
	addr = group[8] - 3;
	for (i = 0; i < temp; i++) {
		sw_data[i] = (int) str[addr + i];
	}
	para_set(ACcfg_Struct, parament[1], sw_data); //��ʱ����������Ĳ���

	temp = group[9] + 3; //the fourth data length
	addr = group[11] - 3;
	for (i = 0; i < temp; i++) {
		cd_data[i] = (int) str[addr + i];
	}
	para_set(ACcfg_Struct, parament[2], cd_data); //��ʱ����������Ĳ���

	temp = group[12] + 3; //the fifth data length
	addr = group[14] - 3;
	for (i = 0; i < temp; i++) {
		wv_data[i] = str[addr + i];
	}
	para_set(ACcfg_Struct, parament[3], wv_data); //��ʱ����������Ĳ���

	temp = group[15] + 3; //the sixth data length
	addr = group[17] - 3;
	for (i = 0; i < temp; i++) {
		wd_data[i] = str[addr + i];
	}
	para_set(ACcfg_Struct, parament[4], wd_data); //��ʱ����������Ĳ���

	/*free all data*/
	//	free(og_data);

	og_data = (int *) calloc(group[0], sizeof(int));
	for (i = 0; i < group[0]; i++) {
		cdata[i] = ACcfg_Struct.x[i];
	}
	parament[5] = group[0];
	length = group[0];
	free(og_data); //string type free this data
	free(cd_data);
	free(sw_data);
	free(te_data);
	free(wd_data);
	free(wv_data);

	free(group);
	return (1);
	//	return(og_data);
}
/**
  * @brief  max
  * @param  data1,data2 is compare data
  * @retval max data
  */
static int get_diff(int data1, int data2)
{
//	int temp = 0;
	if(data1 > data2)
	{
		return(data1 - data2);
	}
	else
	{
		return(data2 - data1);
	}
}
/**
  * @brief  generate head data
  * @param  data: original data from collected data
			head: the head data size is 4 byte
  * @retval
  */
void generate_head_data(short int data[], short int head[])
{
	int i = 0;
	for(i = 0; i < 4; i++)
	{
		head[i] = data[i];
	}

}

/**
  * @brief  generate head size
  * @param  data: original data from collected data
			length: original data length
			head: the head data size is 4 byte
  * @retval
  */
int generate_head_size(short int data[], int length)
{
	int i;
	int *y;
	int *temp;
	int head_size = 0;

	temp = (int *)malloc(length*sizeof(int));
	y = (int *)malloc(length*sizeof(int)/2);

	for(i = 0; i < length; i++)
	{
		temp[i] = data[i];
	}
	y = rem2bit(temp,length);

	head_size = judge_head(y);
	free(y);
	free(temp);
	return(head_size);
}

/**
  * @brief  head_compare
  * @param  data1,data2 is compare data
			size is data length
  * @retval
  */
int head_compare(short int data1[], short int data2[], int size)
{
	int i;
	int *y1;
	int *y2;
	int *temp;
	int head1 = 0;
	int head2 = 0;
	int diff = 0;

	temp = (int *)malloc(size*sizeof(int));
	y1 = (int *)malloc(size*sizeof(int)/2);
	y2 = (int *)malloc(size*sizeof(int)/2);
	for(i = 0; i < size; i++)
	{
		temp[i] = data1[i];
	}
	y1 = rem2bit(temp,size);
	for(i = 0; i < size; i++)
	{
		temp[i] = data2[i];
	}
	y2 = rem2bit(temp,size);
	head1 = judge_head(y1);
	head2 = judge_head(y2);
	if(head1 != head2)
	{
		return(0);
	}
	head1 = head1 * 4;
	for(i = 0; i < head1; i++)
	{
		diff = get_diff(data1[i],data2[i]);
		if(diff > 5)
		{
			return(0);
		}
	}
	free(temp);
	free(y1);
	free(y2);

	return(1);
}

/**
  * @brief  generate level data
  * @param
  * @retval
  */
void generate_level_data(short int data[], int length, short int level[])
{
	int i = 0;
	int *tmp;
	int temp_level[8];

	tmp = (int *)malloc(length*sizeof(int));
	for(i = 0; i < length; i++)
	{
		tmp[i] = (int)data[i];
	}
	get_level_data(tmp, length, temp_level); //get level data

	for(i = 0; i< 8; i++)
	{
		level[i] = (int)temp_level[i];
	}
	free(tmp);
}
/**
  * @brief  level_compare
  * @param  data1,data2 is compare data
			size is data length
  * @retval
  */
int level_compare(short int data1[], short int data2[], int size)
{
	int i = 0;
	int level1[8] = {0};
	int level2[8] = {0};
	int *tmp;
	int diff = 0;

	tmp = (int *)malloc(size*sizeof(int));
	for(i = 0; i < size; i++)
	{
		tmp[i] = data1[i];
	}
	get_level_data(tmp, size, level1); //get level data
	for(i = 0; i < size; i++)
	{
		tmp[i] = data2[i];
	}
	get_level_data(tmp, size, level2); //get level data
	for(i = 0; i < 8; i++)
	{
		diff = get_diff(level1[i],level2[i]);
		if(diff > 5)
		{
			return(0);
		}
	}
	free(tmp);
	return(1);
}

