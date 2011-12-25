package armodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;


public class ArTest {

	public static void main(String[] args) {

		ArTest test = new ArTest();
		
		test.test();

	}
	private void test(){

		AutoRegression ar = new AutoRegression();
		double []inputSeries = readData();
		int degree = 6;
		try{
			double[] coefficients = ar.genCoefficients(inputSeries, degree);
			
			printArray(coefficients);
			
			// predict
			double[] predict = ar.predictVec(inputSeries, coefficients, 100000);
			//printArray(predict);
			
			double[] fixed = new double[inputSeries.length + predict.length];
			System.arraycopy(inputSeries, 0, fixed, 0, inputSeries.length);
			System.arraycopy(predict, 0, fixed, inputSeries.length, predict.length);
			storeArray(fixed, "result_predict.dat");
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void printArray(double[] arr){
		for(int i = 0; i< arr.length; i++){
			System.out.println(arr[i]);
		}
	}
	// ´æÎÄ¼þ
	private void storeArray(double[] arr, String fileName){
		
		System.out.println("storeLines : " + arr.length);
		File file = new File(fileName);
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i< arr.length; i++){
				writer.write(String.valueOf(arr[i]) + "\n");
			}
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private double[] readData(){
		File file = new File("num2.dat");

		ArrayList<Double> list = new ArrayList<Double>();

		double[] _data = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String item = null;
			while ((item = reader.readLine()) != null) {
				double dataItem = Double.valueOf(item);
				list.add(dataItem * 100);
			}

			_data = new double[list.size()];

			Iterator<Double> iter = list.iterator();
			int i = 0;
			while (iter.hasNext()) {
				double num = iter.next();
				_data[i++] = num;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return _data;
	}
}
