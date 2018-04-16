#include<stdio.h>
#include<stdlib.h>
#include<math.h>
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
jint Java_com_infraredctrl_aircodec_AirCodec_getcodeId(JNIEnv* env,jobject obj,jshortArray locationData,jshortArray servierData,jint length){
    jsize src_sz, service_sz;
    jbyte *src, *service;
    jint *parament;
	jint result;
	src_sz = (*env)->GetArrayLength(env, locationData);
	src = (*env)->GetByteArrayElements(env, locationData, JNI_COPY);

	service_sz = (*env)->GetArrayLength(env, servierData);
	service = (*env)->GetByteArrayElements(env, servierData, JNI_COPY);

	 LOGD("result:%d",length);
	 LOGD("src_sz:%d",src_sz);
	 LOGD("service_sz:%d",service_sz);
      result=complete_compare(src,service,src_sz);
 (*env)->ReleaseByteArrayElements(env, locationData, src, JNI_COPY);
 (*env)->ReleaseByteArrayElements(env, servierData, service, JNI_COPY);
   LOGD("result1:%d",result);
return result;
}

jint Java_com_infraredctrl_aircodec_AirCodec_getACID(JNIEnv* env,jobject obj,jintArray src){
	jsize src_sz;
	  jint* arr;
	  jint result;
//	    jint length;
	    arr = (*env)->GetIntArrayElements(env,src,NULL);
//	    length = (*env)->GetArrayLength(env,Attr);
	    result=   get_AC_ID(arr);
	    (*env)->ReleaseIntArrayElements(env, src, arr, JNI_COPY);
	    return result;
}





typedef struct
{
	int cod;
	int sw;
	int tem;
	int wd;
	int wv;
}AC_InitTypeDef;

typedef struct
{
	int high_level[4];
	int low_level[4];
}Level_InitTypeDef;

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

static void take_even(int *x, int *y, int size)
{
	int i = 0;
	/**/
	for(i = 0;i<size/2;i++)
	{
		y[i] = x[2*i+1];
	}
}

/**
  * @brief  SORT DATA
  * @param  x IS AFTER CHANGE DATA
  * @retval y IS HEAD SIZE
  */
static void sort(int *data)
{
	int temp = 0;
	int i;
	int j;

	/*Bubble sort method*/
	for(i = 0; i < 10; i++)
	{
		for(j = i+1; j < 10; j++)
		{
			if(data[i] > data[j])
			{
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
int max_10bit(int *x)
{
	int i;
	int data[10] = {0};
	/*take 10 bit data*/
	for(i = 0; i <10; i++)

	{
		data[i] = x[i];
	}

	sort(data);
	return(data[9]);
}

/**
  * @brief  SORT DATA
  * @param  x IS AFTER CHANGE DATA
  * @retval y IS HEAD SIZE
  */
int min_10bit(int *x)
{
	int i;
	int data[10] = {0};
	/*take 10 bit data*/
	for(i = 0; i <10; i++)

	{
		data[i] = x[i];
	}

	sort(data);
	return(data[0]);

}

/**
  * @brief  TWO CHAR DATA TRANSFORMAT TO INT DATA
  * @param  x is initdata
  *			y is after changed data that size is half of initdata's size
  *			size is initdata size
  * @retval None
  */
int *rem2bit(int *x,int size)
{
	int i;
	int *temp;
	int *y;
	
	/*initial data*/
	temp = (int *)calloc(size,sizeof(int));
	y = (int *)calloc(size/2,sizeof(int));

	/*combination and assignment*/
	for(i = 0; i < size; i++,i++)
	{
		if ((x[i]/128) >= 1)
		{
			temp[i] = x[i] - 128;
		}
		temp[i] = temp[i]*256 + x[i+1];
	}
	for(i = 0;i < size/2 ;i++)
	{
		y[i] = temp[i*2];
	}
	free(temp);

	return(y);
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
int find_values(int *x, int *y, int mode, int value, int size)
{
	int i;
	int j = 0;

	/*select less than value data*/
	if(mode == 0)
	{
		for(i = 0; i < size; i++)
		{
			if(x[i] < value)
			{
				y[j] = i;
				j++;
			}
		}
	}
	/*select equal value data*/
	else if(mode == 1)
	{
		for(i = 0; i < size; i++)
		{
			if(x[i] == value)
			{
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than value data*/
	else if(mode == 2)
	{
		for(i = 0; i < size; i++)
		{
			if(x[i] > value)
			{
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than or equal value data*/
	else if(mode == 3)
	{
		for(i = 0; i < size; i++)
		{
			if(x[i] <= value)
			{
				y[j] = i;
				j++;
			}
		}
	}
	/*select greater than or equal value data*/
	else if(mode == 4)
	{
		for(i = 0; i < size; i++)
		{
			if(x[i] >= value)
			{
				y[j] = i;
				j++;
			}
		}
	}
	return(j);
}

/**
  * @brief  REPLACE DATA
  * @param  x IS 
  *			y is 
  */
void replace_data(int *x, int *adr,int size, int data)
{
	int i = 0;
	for(i = 0;i < size;i++)
	{
		x[adr[i]] = data;
	}
}

/**
  * @brief  find data head size
  * @param  x is after chenge data
  * @retval head is header size 
  */
int judge_head(int *x)
{
	int i;
	int data[10] = {0};
	float load = 0;
	int head_size = 0;

	for(i = 0; i < 10; i++)
	{
		data[i]	= x[i];
	}
	//caculate one bit size
	for(i = 0; i < 5; i++)
	{
		data[i] = data[i] + data[i+1];
	}
	for(i = 0;i < 4; i++)
	{
		load = (float)data[i]/(float)data[0];
		if(load > 0.9)
		{
			head_size++;
		}
	}
	if(head_size>=5)
	{
		head_size = 1;
	}
	return(head_size);
}

/**
  * @brief  TWO CHAR DATA TRANSFORMAT
  * @param  x is after chenge data
  * @retval y is head size 
  */
int *low_level_modulation(int *x, int maxdata,int size)
{
	int i = 0;
	int *adr = 0;
	int *y = 0;
	int count = 0;
	int min = 0;
	int max = 0;
	int mid = 0;
	
	/*initial x array*/
	y = (int *)calloc(size/2,sizeof(int));
	take_even(x,y,size);
	min = min_10bit(y);

	//replace special data
	adr = (int *)calloc(size,sizeof(int));
	maxdata = maxdata * 9 / 10;
	size = size / 2;
	count = find_values(y, adr, 4, maxdata, size);

	replace_data(y, adr, count, min);
	max = max_10bit(y);

	//modulation 
	mid = (max+min)/2;
	for(i = 0; i < size; i++)
	{
		if(y[i] > mid)
		{
			y[i] = 1;
		}
		else
		{
			y[i] = 0;
		}
	}
	free(adr);
	return (y);
	
}

/**
  * @brief  translate data form orignal data to binary data
  * @param  original data
  * @retval y is the binary
  */
int* org2binary(int og_data[], int length)
{
	int i = 0;
	int head = 0;
	int maxdata = 0;
	int *y;
	int *temp;
	int	tmp = 0;
	int *x;

	x = (int *)calloc(length,sizeof(int));
	for(i = 0; i < length; i++)
	{
		x[i] = og_data[i];
	}
	/*initial data*/
	y = (int *)calloc(length/2,sizeof(int));
	temp = (int *)calloc(length/2-2,sizeof(int));

	y = rem2bit(x,length);
	head = judge_head(y);
	maxdata = y[1];

	head = head * 2;				//一个头占2个电平
	length = length/2 - head - 1;	//总长度除2是单个电平，减头，减尾（尾是1）
	//data from y[head*2]~y[end-1]
	for(i = 0; i < length; i++)
	{
		temp[i] = y[i + head];
	}
	free(y);
	y = (int *)calloc(length,sizeof(int));
	y = low_level_modulation(temp,maxdata,length);

	free(x);
	free(temp);
	return(y);
}
/**
  * @brief  get level data
  * @param  original data
  * @retval y is the level data
  */
void get_level_data(int og_data[], int length, int level[8])
{
	int i = 0;
	int head = 0;
	int maxdata = 0;
	int *y;
	int *temp;
	int	tmp = 0;
	int *x;
	x = (int *)calloc(length,sizeof(int));
	for(i = 0; i < length; i++)
	{
		x[i] = og_data[i];
	}
	/*initial data*/
	y = (int *)calloc(length/2,sizeof(int));
	temp = (int *)calloc(length/2-2,sizeof(int));

	y = rem2bit(x,length);
	head = judge_head(y);
	maxdata = y[1];

	head = head * 2;				
	length = length/2 - head - 1;
	//data from y[head*2]~y[end-1]
	for(i = 0; i < length; i++)
	{
		temp[i] = y[i + head];
	}
	free(y);
	y = (int *)calloc(length,sizeof(int));
	y = low_level_modulation(temp,maxdata,length);
	free(temp);
	length = length / 2;
	temp = (int *)calloc(length,sizeof(int));

	/*get low level*/
	tmp = find_values(y, temp, 1, 0, length);
	if(tmp > 0)
	{
		tmp = temp[0]*4;
		tmp = tmp + head*2;
		for(i = 0; i < 4; i++)
		{
			level[i] = x[tmp];
			tmp++;
		}
	}
	free(temp);
	/*get high level*/
	temp = (int *)calloc(length,sizeof(int));
	tmp = find_values(y, temp, 1, 1, length);
	if(tmp > 0)
	{
		tmp = temp[0]*4;
		tmp = tmp + head*2;
		for(i = 0; i < 4; i++)
		{
			level[4 + i] = x[tmp];
			tmp++;
		}
	}

	free(x);
	free(y);
	free(temp);
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
	return(1);
}

/**
  * @brief  level_compare
  * @param  data1 是收集到的数据
			data2 是服务中的完整的数据
			size is data length
  * @retval 
  */

/*还需要修改*/
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

/**
  * @brief  level_compare
  * @param  data1 is collect data
			data2 is Level_Pulse of service data that size is 8 byte.
			size is data length
  * @retval 
  */

/*还需要修改*/
int level_compare_direc(short int data1[], short int data2[], int size)
{
	int i = 0;
	int level1[8] = {0};
	int level2[8] = {0};
	int *tmp;
	int diff = 0;
	//we need chang data type short int to int. Beaceuse all data is use int execute.
	tmp = (int *)malloc(size*sizeof(int));
	for(i = 0; i < size; i++)
	{
		tmp[i] = data1[i];
	}
	get_level_data(tmp, size, level1); //get level data

	for(i = 0; i < 8; i++)
	{
		level2[i] = data2[i];
	}


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
/**
  * @brief  Compare the two number are the same.
  * @param  data1,data2 is compare data
			size is data length
  * @retval mark is a judge parement, 0 stand for inconsistent, 1 stand for unanimous
  */
int compare_data(int data1[], int data2[], int size, int site[])
{
	int i;
	int diff = 0;

	for(i = 0; i < size; i++)
	{
		diff = get_diff(data1[site[i]-1],data2[site[i]-1]);
		LOGD("*******i:%d",i);
		LOGD("^^^^^^^^^^^^diff:%d",diff);
		if(diff > 0)
		{
			return(0);
		}
	}
	return(1);
}
/**
  * @brief  level_compare
  * @param  data1 is clien data
			data2 is service data
			size is data length
  * @retval mark is a judge parement, 0 stand for inconsistent, 1 stand for unanimous
  */
int complete_compare(short int data1[], short int data2[], int size)
{
	int *tmp;
	int *temp1;
	int *temp2;
	int i;
	int diff = 0;
	int count = 0;
	int mark = 0;
	int length = 0;
	int *site;

	tmp = (int *)malloc(size*sizeof(int));
	temp1 = (int *)malloc(size*sizeof(int));
	temp2 = (int *)malloc(size*sizeof(int));
	site = (int *)malloc(size/4*sizeof(int));
	/*从data2中分离出需要的数据,位数据保存在site，原始数据放在data2*/
	i = 2;
	while( mark == 0 )
	{
		if( (data2[0] == 0xff && data2[1] == 0xff) )
		{
			length = data2[2];
			if( (data2[3] == 0xff && data2[3] == 0xff) )
			{
				if( (data2[length+5] == 0xff && data2[length+6] \
					 && data2[length+7] == 0xff && data2[length+8]) )
				{
					 for(i = 0;i < length; i++)
					 {
						site[i] = data2[i + 5];
					 }
					 for(i = 0;i < size; i++)
					 {
						tmp[i] = data2[length + 9 + i];
					 }
					 mark = 1;
				}
			}
		}
	}


	temp2 = org2binary(tmp, size);//get digital data

	/*最开始要有个分离 data2的过程，把data2中的位数据和已经有的数据提取出来*/

	for(i = 0; i < size; i++)
	{
		tmp[i] = data1[i];
	}
	temp1 = org2binary(tmp, size);		//get digital data

	for(i = 0; i < length; i++)
	{
		LOGD("*******i:%d",i);
		LOGD("^^^^^^^^^^^^site:%d",site[i]);
	}
	/*此处有个位比较 选择指定的位进行比较，而不是这样进行组个进行比较*/
	if( compare_data(temp1, temp2, length, site))
	{
		return(1);
	}
	else
	{
		return(0);
	}
}
/**
  * @brief  get data
  * @param  str is packet from studied infrared data
			length is packet length
			cdata is return data  that size as str's size.
  * @retval 
  */
/*
int get_data(short int str[], int length, int parament[6], short int cdata[])
{

}
*/
/**
  * @brief  caculate the control id
  * @param  
  *			
  *			
  * @retval 
  */
int get_AC_ID(int parament[5])
{
	int i = 0;
	int ID = 0;
	AC_InitTypeDef AC_Struct;

	AC_Struct.cod = parament[2];
	AC_Struct.sw = parament[1];
	AC_Struct.tem = parament[0];
	AC_Struct.wd = parament[4];
	AC_Struct.wv = parament[3];

	switch(AC_Struct.wv)
	{
		case 0:AC_Struct.wv = 3;break;
		case 1:AC_Struct.wv = 0;break;
		case 2:AC_Struct.wv = 1;break;
		case 3:AC_Struct.wv = 2;break;
	}
	if(AC_Struct.sw != 1)
	{
		ID = 0;
	}
	else
	{
		switch(AC_Struct.cod)
		{
			case 0:ID = AC_Struct.wd*60 + AC_Struct.wv * 15 + AC_Struct.tem + 1;break;
			case 1:ID = AC_Struct.wd*60 + AC_Struct.wv * 15 + AC_Struct.tem + 121;break;
			case 2:ID = AC_Struct.wd*4 + AC_Struct.wv + 241;break;
			case 3:ID = 249;break;
			case 4:ID = 250;break;
			default:ID = 251;break;
		}
	}
	return(ID);
}
