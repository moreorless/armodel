package armodel;

public class AutoRegression {

//	/**
//	 * ��ʼ������
//	 */
//	private double[] inputSeries;
//	/**
//	 * ����
//	 */
//	private int degree;
//	
//	public AutoRegression(double[]inputSeries, int degree){
//		this.inputSeries = inputSeries;
//		this.degree = degree;
//	}
	
	
	/**
	 * ����ʷ��������AR����
	 * @param inputSeries	��ʷ����
	 * @param degree		����
	 */
	public double[] genCoefficients(double[] inputSeries, int degree) throws Exception{
		
		/* Determine and subtract the mean from the input series */
		// ��ֵ
		double mean = average(inputSeries);
		// Input Series - mean
		int len = inputSeries.length;
		double[] w = new double[len];
		
		for(int i = 0; i < len; i++){
			w[i] = inputSeries[i] - mean;
		}
		
		/* Perform the appropriate AR calculation */
		// ʹ����С���˼���LEASTSQUARES
		
		return ARLeastSquare(w, degree);
	}
	
	/**
	 * ��С���˷�����AR����
	 * @param inputSeries		ȥ��ֵ�����������
	 * @param degree			����
	 * @return
	 */
	public double[] ARLeastSquare(double[] inputSeries, int degree) throws Exception{
		// ��������		
		double[] coefficients = new double[degree];
		
		double[][] matrix = new double[degree][degree];
		
		for(int i = 0; i < degree; i++){
			coefficients[i] = 0.0;
			for(int j = 0; j < degree; j++){
				matrix[i][j] = 0.0;
			}
		}
		
		int length = inputSeries.length;
		for(int i = degree-1; i < length-1; i++){
			int hi = i + 1;
			for(int j = 0; j < degree; j++){
				int hj = i - j;
				coefficients[j] += (inputSeries[hi] * inputSeries[hj]);
				
				for(int k = j; k < degree; k++){
					matrix[j][k] += (inputSeries[hj] * inputSeries[i-k]);
				}
			}
			
		}
		
		for(int i = 0; i < degree; i++){
			coefficients[i] /= (length - degree);
			for (int j=i; j<degree; j++) {
		         matrix[i][j] /= (length - degree);
		         matrix[j][i] = matrix[i][j];
		      }
		}
		
		/* Solve the linear equations */
		SolveLE(matrix, coefficients);
		
		return coefficients;
	}
	
	/**
	 * Gaussian elimination solver
	 * @param mat
	 * @param vec
	 * @return
	 * @throws Exception 
	 */
	public boolean SolveLE(double[][]mat, double[]vec) throws Exception{
		
		int n = vec.length;
		for(int i=0; i<n-1; i++){
			// ���ֵ
			double max = Math.abs(mat[i][i]);
			int maxi = i;
			
			double h = 0;
			for(int j = i+1; j < n; j++){
				if((h = Math.abs(mat[j][i])) > max){
					max = h;
					maxi = j;
				}
			}
			if(maxi != i){
				double[] mswap 	= mat[i];
		        mat[i] 			= mat[maxi];
		        mat[maxi] 		= mswap;
		        double vswap    = vec[i];
		        vec[i]    		= vec[maxi];
		        vec[maxi] 		= vswap;
			}
			
			double[] hvec = mat[i];
			double pivot = hvec[i];
			
			if(Math.abs(pivot) == 0.0){
				System.err.println("Singular matrix - fatal!");
				throw new Exception("Singular matrix - fatal!");
			}
			
			for (int j=i+1; j<n; j++) {
		         double q = - mat[j][i] / pivot;
		         mat[j][i] = 0.0;
		         for (int k=i+1; k<n; k++)
		            mat[j][k] += q * hvec[k];
		         vec[j] += (q * vec[i]);
		    }
			
		}
		
		vec[n-1] /= mat[n-1][n-1];
		for (int i=n-2;i>=0;i--) {
		     double[] hvec = mat[i];
		     for (int j=n-1;j>i;j--){
		    	 vec[i] -= (hvec[j] * vec[j]);
		     }
		     vec[i] /= hvec[i];
		  }
		
		return true;
	}
	
	
	/**
	 * ����Ԥ��
	 * @param inputSeries		��������
	 * @param coefficients		AR����
	 * @param predictLen			Ԥ�ⳤ��
	 */
	public double[] predictVec(double[] inputSeries, double[]coefficients, int predictLen){
		
		/* Determine and subtract the mean from the input series */
		// ��ֵ
		double mean = average(inputSeries);
		// Input Series - mean
		int len = inputSeries.length;
		double[] w = new double[len];
		
		for(int i = 0; i < len; i++){
			w[i] = inputSeries[i] - mean;
		}
		
		/* Generate a series from the coefficients */
		double []predict = new double[predictLen];
		double rmserror = 0;
		for(int i = 0; i < predict.length; i++){
			double est = 0;
			if(i > coefficients.length){
				for(int j = 0; j < coefficients.length; j++){
					est += coefficients[j] * w[i-j-1];
				}
				rmserror += (est - w[i]) * (est - w[i]);
			}
			
			predict[i] = est;
		}
		
		System.out.println("Len=" + predictLen + ", order=" + coefficients.length
				+ ", sqrt=" + Math.sqrt(rmserror/predictLen));
		
		return predict;
	}
	
	private double average(double[] dataseries){
		
		double mean = 0;
		int len = dataseries.length;
		for(int i = 0; i< len; i++){
			mean += dataseries[i];
		}
		return mean / len;
	}
	
}
