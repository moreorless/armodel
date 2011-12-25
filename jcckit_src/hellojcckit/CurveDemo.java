package hellojcckit;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import jcckit.GraphicsPlotCanvas;
import jcckit.data.DataCurve;
import jcckit.data.DataPlot;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

/**
 * 
 * 
 * @author Franz-Josef Elmer
 */
public class CurveDemo extends Applet {
	private double[] _data = new double[] { 32, 18, 90, 39, 36, 21, 24, 90, 39,
			90, 28, 48, 45.603493, 51.11379, 63.686195, 73.757935 };
	private DataPlot _dataPlot;
	private GraphicsPlotCanvas plotCanvas; 

	private int maxValue = 10;
	private int minValue = 0;
	
	private String _dataFile = "num.dat";
	
	private Frame frame;

	public void init() {
		plotCanvas = createPlotCanvas();

		_dataPlot = new DataPlot();

		DataCurve curve = new DataCurve("curve");
		for (int i = 0; i < _data.length; i++) {
			double x = i;
			double y = _data[i];
			curve.addElement(new DataPoint(x, y));
		}
		_dataPlot.addElement(curve);

		plotCanvas.connect(_dataPlot);

		setLayout(new BorderLayout());
		add(plotCanvas.getGraphicsCanvas(), BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);
	}

	private GraphicsPlotCanvas createPlotCanvas() {
		Properties props = new Properties();
		ConfigParameters config = new ConfigParameters(
				new PropertiesBasedConfigData(props));
		props.put("plot/legendVisible", "false");
		props.put("plot/coordinateSystem/xAxis/minimum", "-0.5");
		props.put("plot/coordinateSystem/xAxis/maximum",
				String.valueOf(_data.length));
		props.put("plot/coordinateSystem/xAxis/axisLabel", "");
		// props.put("plot/coordinateSystem/xAxis/ticLabelFormat/className",
		// "jcckit.plot.TicLabelMap");
		props.put("plot/coordinateSystem/xAxis/ticLabelFormat", "%d");
		props.put("plot/coordinateSystem/yAxis/axisLabel", "AR Model");
		props.put("plot/coordinateSystem/yAxis/maximum",
				String.valueOf(maxValue));
		props.put("plot/coordinateSystem/yAxis/minimum",
				String.valueOf(minValue));
		props.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%d%%");
		props.put("plot/curveFactory/definitions", "curve");
		props.put("plot/curveFactory/curve/withLine", "true");
		// props.put("plot/curveFactory/curve/symbolFactory/className",
		// "jcckit.plot.BarFactory");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/className",
				"jcckit.graphic.ShapeAttributes");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/fillColor",
				"0xfe8000");
		props.put("plot/curveFactory/curve/symbolFactory/attributes/lineColor",
				"0");
		props.put("plot/curveFactory/curve/symbolFactory/size", "0.08");
		props.put("plot/initialHintForNextCurve/className",
				"jcckit.plot.PositionHint");
		props.put("plot/initialHintForNextCurve/position", "0 0.1");

		return new GraphicsPlotCanvas(config);
	}

	private void reDrawCanvas(){

		removeAll();
		
		prepareData();
		
		plotCanvas = createPlotCanvas();

		_dataPlot = new DataPlot();

		DataCurve curve = new DataCurve("curve");
		for (int i = 0; i < _data.length; i++) {
			double x = i;
			double y = _data[i];
			curve.addElement(new DataPoint(x, y));
		}
		_dataPlot.addElement(curve);

		plotCanvas.connect(_dataPlot);

		setLayout(new BorderLayout());
		add(plotCanvas.getGraphicsCanvas(), BorderLayout.CENTER);
		add(createControlPanel(), BorderLayout.SOUTH);
		
		frame.resize(getWidth(), getHeight());
	}
	
	private Panel createControlPanel() {
    Panel controlPanel = new Panel();
    
    
    
    Button startButton = new Button("animate");
    startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  new Thread() {
                          public void run() {
                            animate();
                          }
                        }.start();
                }
              });
    controlPanel.add(startButton);
    
    Button fileButton = new Button("open file");
    fileButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			FileDialog fDialog = new FileDialog(findParentFrame(), "open file");
			fDialog.setVisible(true);
			_dataFile = fDialog.getFile();
			
			reDrawCanvas();
		}
	});
    controlPanel.add(fileButton);
    
    return controlPanel;
  }

	private Frame findParentFrame() {
		Container c = this;
		while (c != null) {
			if (c instanceof Frame)
				return (Frame) c;

			c = c.getParent();
		}
		return (Frame) null;
	}

	private void animate() {
		DataCurve curve = new DataCurve("");
		for (int i = 0; i < _data.length; i++) {
			double x = i;
			double y = _data[i];

			curve.addElement(new DataPoint(x, y));
		}
		_dataPlot.replaceElementAt(0, curve);
	}

	private void prepareData() {
		System.out.println(Thread.currentThread().getContextClassLoader()
				.getResource(""));
		System.out.println(new File("./").getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));

		// File file = new File("result_predict.dat");
		File file = new File(_dataFile);
		System.out.println(file.getAbsolutePath());

		ArrayList<Double> list = new ArrayList<Double>();
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
			
			maxValue = 0;
			minValue = 0;
			while (iter.hasNext()) {
				double num = iter.next();
				if (maxValue < num) {
					maxValue = (int) num + 1;
				}

				if (minValue > num) {
					minValue = (int) num;
				}

				_data[i++] = num;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		CurveDemo applet = new CurveDemo();
		applet.startMain();
	}
	private void startMain(){
		frame = new Frame("ARÄ£Äâ");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		prepareData();

		init();
		frame.add(this);
		frame.setSize(1200, 800);
		frame.show();
	}

}
