package com.infraredctrl.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Display;

public class CharUtil {
	public static GraphicalView getCurveIntent(Context context, int[] colors, String[] titles, String[] strs, PointStyle[] styles, String yTime, List<double[]> x, List<double[]> values, int xmax, Display display, int axisMin, int axisMax, int lineNumber) { // 得到用于显示曲线图的intent
		// String[] titles = new String[] { "","" }; // 图最下面显示的数据，表示曲线的名字
		// int[] colors = new int[] { Color.GREEN ,Color.RED}; //
		// 用于曲线设置曲线的颜色和曲线的名字颜色
		// PointStyle[] styles = new PointStyle[] {
		// PointStyle.CIRCLE,PointStyle.CIRCLE }; // 曲线的样式,有多种
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(); // 最主要的一个对象，用于设置曲线的很多参数配置
		renderer.setAxisTitleTextSize(display.getWidth() / 25); // 设置表示x轴，Y轴信息文本的大小
		renderer.setChartTitleTextSize(display.getWidth() / 25); // 曲线图说明文字的大小
		renderer.setLabelsTextSize(display.getWidth() / 30); // x,y轴上面坐标文本的字体大小
		renderer.setLegendTextSize(display.getWidth() / 30); // 设置下方表示曲线名字的文本字体大小
		renderer.setGridColor(Color.WHITE); // 设置网格边框的颜色
		renderer.setPointSize(display.getWidth() / 120); // 设置曲线上面小图形的大小
		renderer.setMargins(new int[] { display.getHeight() / 21, display.getWidth() / 11, 25, 15 }); // 数据分别为曲线图离屏幕的上左下右的间距
		// renderer.setXLabels(15); // 网格x轴的大概条数
		renderer.setYLabels(lineNumber); // 网格Y轴的大概条数
		renderer.setShowGrid(true); // 是否显示网格
		renderer.setApplyBackgroundColor(true); // 是否可以设置背景颜色，默认为false
		renderer.setMarginsColor(Color.rgb(31, 187, 166));// 设置外面背景色，坐标颜色也会随着变话，不知道什么情况
		renderer.setBackgroundColor(Color.rgb(31, 187, 166)); // 设置网格背景颜色，需要和上面属性一起用
		renderer.setYLabelsAlign(Align.RIGHT); // 以Y轴的哪个地方对其
		// renderer.setZoomButtonsVisible(true); // 是否显示右下角的放大缩小还原按钮
		renderer.setPanLimits(new double[] { 1, 10, -10, 40 }); // 滚动後显示的数据，和这里设置一样则不滚动
		// renderer.setZoomLimits(new double[] { 100, 50, 20, 5 }); //
		// 点击放大缩小时候使用
		renderer.setChartTitle("室内温度统计图"); // 设置种上文本类荣
		renderer.setXTitle(yTime); // 设置x轴名字
		renderer.setYTitle("温度/℃"); // y轴名字
		renderer.setXAxisMin(0); // x轴坐标最小值setXLabels
		renderer.setXAxisMax(xmax); // x轴坐标最大值
		renderer.setYAxisMin(axisMin); // y轴最小值
		renderer.setYAxisMax(axisMax); // y值最大值
		renderer.setLabelsColor(Color.WHITE);
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		renderer.setAxesColor(Color.WHITE); // x，y轴的颜色
		// ===========================================================

		int j = 0;
		renderer.setXLabels(0); // 设置X轴不显示数字（改用我们手动添加的文字标签）
		for (String date : strs) {
			renderer.setLabelsColor(Color.WHITE);
			renderer.addTextLabel(j, date); // 修改X轴显示坐标
			j++;
		}
		// ============================================================
		int length1 = colors.length;
		for (int i = 0; i < length1; i++) { // 设置颜色,绘制曲线
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setLineWidth(5);
			renderer.addSeriesRenderer(r);
		}
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset(); // 设置图下面显示的信息
		int length2 = titles.length;
		for (int i = 0; i < length2; i++) {
			XYSeries series = new XYSeries(titles[i], 0);
			double[] xV = x.get(i);
			double[] yV = values.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		GraphicalView mChartView = ChartFactory.getTimeChartView(context, dataset, renderer, "yy/MM");
		return mChartView;
	}

	// =====================================================================时间推导=============
	/**
	 * 判断是闰年还是平年
	 * 
	 * @Title judge
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 上午9:14:28
	 * @param year
	 * @return
	 */
	public static boolean judge(int year) {
		boolean yearleap = (year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0);// 采用布尔数据计算判断是否能整除
		return yearleap;
	}

	/**
	 * 判断那一个月有集体那
	 * 
	 * @Title calculate
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午5:42:30
	 * @param year
	 * @param month
	 * @return
	 */
	public static int calculate(int year, int month) {
		boolean yearleap = judge(year);
		int day;
		if (yearleap && month == 2) {
			day = 29;
		} else if (!yearleap && month == 2) {
			day = 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		} else {
			day = 31;
		}
		return day;
	}

	/**
	 * 传入一个星期几返回一个从那天起的前一个星期的时间
	 * 
	 * @Title getStrX
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 上午9:13:15
	 * @param nowWeek
	 * @return
	 */
	public static String[] getStrX() {
		String nowWeek = getWeekOfDate();
		String[] strContent = new String[8];
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		strContent[0] = "";
		for (int i = 0; i < weekDays.length; i++) {
			if (weekDays[i].equals(nowWeek)) {
				for (int j = weekDays.length; j > 0; j--) {
					for (int k = i; k >= 0; k--, j--) {
						if (k == 0 && j == 1) {
							strContent[j] = weekDays[k];
							break;
						} else if (k == 0 && j > 0) {
							strContent[j] = weekDays[k];
							j--;
							for (int m = weekDays.length - 1; m > 0; m--, j--) {
								if (j == 1) {
									strContent[j] = weekDays[m];
									return strContent;
								}
								strContent[j] = weekDays[m];
								System.out.println("^^^^^^^j:" + j + "^^^^^:" + strContent[j]);
							}
						}
						strContent[j] = weekDays[k];
						System.out.println("#######j:" + j + "####:" + strContent[j]);
					}
				}
			}
		}
		return strContent;
	}

	/**
	 * 根据需要传入一个整数表示需要一天24小时的数据还是一个月30天的数据，然后得到一个 数组表示x轴坐标
	 * 
	 * @Title showTimeData
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-17 上午9:14:40
	 * @param number
	 * @return
	 */
	public static String[] showTimeData(int number) {
		Map<String, Object> timeMap = new HashMap<String, Object>();
		timeMap = getNowTime();
		String[] strData = null;
		if (number == 24) {
			strData = gethour(timeMap.get("hour").toString());
		} else {
			strData = getMonth(Integer.parseInt(timeMap.get("year").toString()), Integer.parseInt(timeMap.get("month").toString()), timeMap.get("day").toString());
		}

		String[] strTime = new String[strData.length + 1];
		strTime[0] = "";
		for (int i = 0; i < strData.length; i++) {
			if (Integer.parseInt(strData[i].toString()) % 2 != 0) {
				strTime[i + 1] = strData[i];
			} else {
				strTime[i + 1] = "";
			}
		}
		return strTime;
	}

	/**
	 * 传入年月日，返回从那天起的前30天的时间排列
	 * 
	 * @Title getMonth
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 上午9:15:53
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String[] getMonth(int year, int month, String day) {
		int number = calculate(year, month - 1);// 得到上一个月有几天
		String[] strMonth = new String[30];
		String[] strData = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
		for (int i = 0; i < strData.length; i++) {
			// if (day.equals(strData[i])) {
			if (Integer.parseInt(day) == Integer.parseInt(strData[i])) {
				for (int j = strMonth.length - 1; j >= 0; j--, i--) {
					if (j == 0 && i == 0) {
						// 那一天刚好是28，或是29，或是30或是31
						strMonth[j] = strData[i];
						return strMonth;
					} else if (i == 0 && j > 0) {
						// 处理上当前那个月的最后一天
						strMonth[j] = strData[i];
					} else if (i < 0 && j >= 0) {
						// 处理上一个月的时间
						// 判断上一个月有几天
						if (number == 28) {
							for (int k = strData.length - 4; k >= 0; k--, j--) {
								// 从第28个数据取
								strMonth[j] = strData[k];
								if (j == 0) {
									return strMonth;
								}
							}
						} else if (number == 29) {
							for (int k = strData.length - 3; k >= 0; k--, j--) {
								// 从第29个数据取
								strMonth[j] = strData[k];
								if (j == 0) {
									return strMonth;
								}
							}
						} else if (number == 30) {
							for (int k = strData.length - 2; k >= 0; k--, j--) {
								// 从第30个数据取
								strMonth[j] = strData[k];
								if (j == 0) {
									return strMonth;
								}
							}
						} else if (number == 31) {
							for (int k = strData.length - 1; k >= 0; k--, j--) {
								// 从第31数据取
								strMonth[j] = strData[k];
								if (j == 0) {
									return strMonth;
								}
							}
						}
					} else {
						strMonth[j] = strData[i];
					}
				}
			}
		}
		return strMonth;
	}

	/**
	 * 得到从那一时刻起的前24小时的时间排列
	 * 
	 * @Title getMonth
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-10 下午2:25:17
	 * @param houre
	 * @return
	 */
	public static String[] gethour(String houre) {
		String[] data = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "00", };
		String[] dayTimeData = new String[data.length];
		int intHoure = Integer.parseInt(houre);
		if (0 < intHoure && intHoure < 10) {
			houre = houre.replaceAll("0", "").trim();
		}
		for (int i = 0; i < dayTimeData.length; i++) {
			if (houre.equals(data[i])) {
				for (int j = i, k = dayTimeData.length - 1; j >= 0 && k >= 0; j--, k--) {
					dayTimeData[k] = data[j];
					if (j == 0 && k > 0) {
						for (int m = dayTimeData.length - 1, h = k - 1; m >= 0 && h >= 0; m--, h--) {
							dayTimeData[h] = data[m];
							if (h == 0) {
								return dayTimeData;
							}
						}
					} else if (j == 0 && k == 0) {
						return dayTimeData;
					}
				}
			}
		}
		return dayTimeData;
	}

	/**
	 * 快速排序算法,返回排序后那个数组的最小值和最大值
	 * 
	 * @Title quickSort
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午12:51:22
	 * @param a
	 * @param left
	 * @param right
	 */
	// public static double[] quickSort(double a[], int left, int right) {
	// double[] data = new double[2];
	// if (right-left<=1) {
	// //当只有一个数据的时候最大和最小值相等
	// data[0] = a[0];
	// data[1] = a[0];
	// return data;
	// }
	// int i, j, temp;
	// i = left;
	// j = right;
	// if (left > right)
	// return null;
	// temp = (int) a[left];
	// while (i != j)/* 找到最终位置 */
	// {
	// while (a[j] >= temp && j > i)
	// j--;
	// if (j > i)
	// a[i++] = a[j];
	// while (a[i] <= temp && j > i)
	// i++;
	// if (j > i)
	// a[j--] = a[i];
	// }
	// a[i] = temp;
	// quickSort(a, left, i - 1);/* 递归左边 */
	// quickSort(a, i + 1, right);/* 递归右边 */
	// data[0] = a[left];
	// data[1] = a[right];
	// return data;
	// }
	public static double[] quickSort(double a[], int left, int right) {
		double[] data = new double[2];
		int i, j, temp;
		i = left;
		j = right;
		if (left >= right) {
			data[0] = a[0];
			data[1] = a[a.length - 1];
			return data;
		}

		temp = (int) a[left];
		while (i != j)/* 找到最终位置 */
		{
			while (a[j] >= temp && j > i)
				j--;
			if (j > i)
				a[i++] = a[j];
			while (a[i] <= temp && j > i)
				i++;
			if (j > i)
				a[j--] = a[i];

		}
		a[i] = temp;
		quickSort(a, left, i - 1);/* 递归左边 */
		quickSort(a, i + 1, right);/* 递归右边 */

		data[0] = a[0];
		data[1] = a[a.length - 1];
		return data;
	}

	/**
	 * 得到今天星期几
	 * 
	 * @Title getWeekOfDate
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午1:06:31
	 * @return
	 */
	public static String getWeekOfDate() {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * 返回当前的日期时间
	 * 
	 * @Title getNowTime
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午1:36:49
	 * @return
	 */
	public static Map<String, Object> getNowTime() {
		Map<String, Object> timeMap = new HashMap<String, Object>();
		java.util.Calendar c = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		String time = (f.format(c.getTime())).toString();
		System.out.println(time + "==========");
		timeMap.put("year", time.substring(0, time.indexOf("年")));
		timeMap.put("month", time.substring(time.indexOf("年") + 1, time.indexOf("月")));
		timeMap.put("day", time.substring(time.indexOf("月") + 1, time.indexOf("日")));
		timeMap.put("hour", time.substring(time.indexOf("日") + 1, time.indexOf("时")));
		timeMap.put("minute", time.substring(time.indexOf("时") + 1, time.indexOf("分")));
		return timeMap;
	}

	/**
	 * 将原数据按照要求分包，7个或30个
	 * 
	 * @Title getBackage
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午2:13:22
	 * @param allContent
	 *            所有的数据
	 * @param nowHour
	 *            现在是几点
	 * @param number
	 *            包的个数，
	 * @return backageLen 每一个包的长度
	 */
	// public static List<double[]> getBackage(double[] allContent, String
	// nowHour, int number, int backageLen) {
	// // 得到的数据包放到一个list集合中，越新的数据越放在前面
	// List<double[]> listContent = new ArrayList<double[]>();
	// int timeHour = 0;
	// if (nowHour.equals("23")) {
	// // 整点0点
	// timeHour = 0;
	// for (int j = number; j > 0; j--) {
	// double[] backage = new double[backageLen];
	// for (int i = 0, n = backageLen - 1; i < allContent.length && n >= 0; i++,
	// n--) {
	// backage[n] = allContent[i];
	// }
	// listContent.add(backage);
	// }
	// return listContent;
	// } else {
	// timeHour = Integer.parseInt(nowHour);
	// double[] fristBackage = new double[timeHour+4];
	// //这里一天的时间是从0开始 0~23
	// for (int i = 0, j = (timeHour+4) ; i < allContent.length; i++, j--) {
	// if (j >= 0) {
	// fristBackage[j] = allContent[i];
	// } else {
	// listContent.add(fristBackage);
	// for (int k = number - 1; k > 0; k--) {
	// double[] backage = new double[backageLen];
	// for (int m = i, n = backageLen - 1; m < allContent.length && n >= 0; m++,
	// n--) {
	// backage[n] = allContent[m];
	// }
	// listContent.add(backage);
	// }
	// // 结束
	// return listContent;
	// }
	// }
	// return listContent;
	// }
	// }
	public static List<double[]> getBackage(double[] allContent, String nowHour, int number, int backageLen) {
		List<double[]> listContent = new ArrayList<double[]>();
		double[] backageContent = null;
		int tem = 0;
		for (int j = 0; j < number; j++) {
			backageContent = new double[28];
			int backagLen = 27;

			for (int i = tem; i < allContent.length && backagLen >= 0; i++, backagLen--) {
				// for (int k = backageContent.length-1; k >=0 ; k--) {
				//
				// }
				backageContent[backagLen] = allContent[i];
				if (allContent[i] == -128.0) {
					backagLen = 0;
				}
				tem++;
			}
			listContent.add(backageContent);

		}

		return listContent;
	}

	/**
	 * 得到当前查看阶段的每一天的数据的最大和最小值
	 * 
	 * @Title getLowestHegth
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-7 下午4:29:51
	 * @param backageData
	 * @return
	 */
	public static List<double[]> getLowestHegth(double[] allContent, String hour, int backageNumber, int backageLen) {
		List<double[]> backageData = getBackage(allContent, hour, backageNumber, backageLen);
		List<double[]> fianllBackage = removeOverNumber(backageData);
		List<double[]> result = null;
		double[] lowes = new double[fianllBackage.size()];
		double[] height = new double[fianllBackage.size()];
		result = new ArrayList<double[]>();
		// 这里把最新的数据包得到的最大和最小值放到最后面
		for (int i = fianllBackage.size() - 1, j = 0; i >= 0 && j < fianllBackage.size(); i--, j++) {
			// double[] backageTwo=null;
			if (fianllBackage.get(i) != null && fianllBackage.get(i).length > 0) {
				double[] backageTwo = quickSort(fianllBackage.get(i), 0, fianllBackage.get(i).length - 1);
				// 对异常情况处理
				// 最低不低于-50
				if (backageTwo[0] < -50) {
					if (j < 1) {
						lowes[j] = 20;
					} else {
						lowes[j] = lowes[j - 1];
					}
				} else {
					lowes[j] = backageTwo[0];
				}
				// 最高不高于100
				if (backageTwo[1] > 100) {
					if (j < 1) {
						height[j] = 20;
					} else {
						height[j] = height[j - 1];
					}
				} else {
					height[j] = backageTwo[1];
				}

			} else {
				lowes[j] = 20;
				height[j] = 20;
			}
		}

		result.add(lowes);
		result.add(height);
		return result;
	}

	/**
	 * 去除每一个包里多余的时间数据
	 * 
	 * @Title removeOverNumber
	 * @Description TODO
	 * @author ouArea
	 * @date 2014-3-17 上午9:45:45
	 * @param allBackage
	 * @return
	 */
	public static List<double[]> removeOverNumber(List<double[]> allBackage) {
		List<double[]> result = new ArrayList<double[]>();
		for (int i = 0; i < allBackage.size(); i++) {
			double[] tem = allBackage.get(i);
			if (i == 0) {
				// 第一个包去除里面的空0
				double[] oneBackage = null;
				for (int k = 0; k < tem.length; k++) {
					if (tem[k] == -128.0) {
						oneBackage = new double[24 - k];
						for (int j = 0, m = k + 4; j < oneBackage.length; j++, m++) {
							oneBackage[j] = tem[m];
						}
					}
				}
				result.add(oneBackage);
			} else {
				double[] oneBackage = new double[tem.length - 4];
				// 每一个包的0~3是放着时间无效数据
				for (int j = oneBackage.length - 1, k = tem.length - 1; j >= 0 && k >= 0; j--, k--) {
					oneBackage[j] = tem[k];
				}
				result.add(oneBackage);

			}

		}
		return result;

	}
}
