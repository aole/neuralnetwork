package aole;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Network {
	ArrayList<NetworkListener> listeners = new ArrayList<>();
	DefaultCategoryDataset errors = new DefaultCategoryDataset();
	private static int errorcount = 0;

	ArrayList<Layer> layers = new ArrayList<>();
	Layer outputLayer, inputLayer;
	int currentRow = -1;

	double data[][], train[][], test[][];
	int target[][];
	String labels[];
	double learningRate = .2;
	int totalTraining = 0;
	private int numtrain;
	private int epoch;
	double result;

	static DecimalFormat df = new DecimalFormat("#.####");

	public Network(double inputs[][], int hiddens, int outputs[][]) {
		data = inputs;
		target = outputs;

		outputLayer = new Layer(Layer.OUTPUT, outputs[0].length);
		Layer hl = new Layer(Layer.HIDDEN, hiddens, outputLayer);
		inputLayer = new Layer(Layer.INPUT, inputs[0].length, hl);
		layers.add(inputLayer);
		layers.add(hl);
		layers.add(outputLayer);
	}

	public void init(double[][] inputData, int numHidden, String[] targetLabels, int[][] targetData) {
		data = inputData;
		target = targetData;
		labels = targetLabels;
		totalTraining = 0;
		errors.clear();

		outputLayer = new Layer(Layer.OUTPUT, targetData[0].length, null, targetLabels);
		Layer hl = new Layer(Layer.HIDDEN, numHidden, outputLayer);
		inputLayer = new Layer(Layer.INPUT, inputData[0].length, hl);

		layers.clear();
		layers.add(inputLayer);
		layers.add(hl);
		layers.add(outputLayer);
	}

	public int getNumLayers() {
		return layers.size();
	}

	public ArrayList<Layer> getlayers() {
		return layers;
	}

	public void addListener(NetworkListener l) {
		listeners.add(l);
	}

	private void notifyListeners(String msg) {
		for (NetworkListener nl : listeners) {
			nl.networkUpdated(msg);
		}
	}

	private static String arrayToString(double d[]) {
		String s = "[";

		for (double dbl : d) {
			s += df.format(dbl) + ", ";
		}
		s = s.substring(0, s.length() - 2);
		return s + "]";
	}

	public void trainTo(int count, int updateEvery) {
		Layer il, ol;
		double error = 0;
		int dn = 0;
		int finalcount = count + totalTraining;
		for (; totalTraining < finalcount; totalTraining++) {
			dn = totalTraining % data.length;
			// set input data
			il = inputLayer;
			il.setOutputs(data[dn]);
			// feed forward
			for (int ln = 1; ln < layers.size(); ln++) {
				ol = layers.get(ln);
				ol.resetInputs();
				il.feedForward(ol);
				ol.transfer();
				il = ol;
			}
			// back propagate
			ol = outputLayer;
			// output layer calculate distance to target
			error = ol.calculateError(target[dn]);

			for (int bln = layers.size() - 2; bln >= 0; bln--) {
				il = layers.get(bln);
				il.backpropagate(ol, learningRate);
				il.calculateError(ol);
				ol = il;
			}
			if (totalTraining % updateEvery == 0) {
				errors.addValue(error, "Error", errorcount++ + "");
				currentRow = dn;
				notifyListeners("(" + dn + ") " + arrayToString(data[dn]) + " = " + Arrays.toString(target[dn]));
			}
		}
	}

	public CategoryDataset getErrorDataset() {
		return errors;
	}

	public void resetWeights(int seed) {
		Random random = new Random(seed);
		totalTraining = 0;

		for (Layer l : layers) {
			for (Node n : l.nodes) {
				for (int i = 0; i < n.weights.size(); i++) {
					n.weights.set(i, random.nextDouble());
				}
			}
		}
	}

	public void setParameters(int epoch, double percent) {
		this.epoch = epoch;
		numtrain = (int) (data.length * percent);
		train = new double[numtrain][];
		test = new double[data.length - numtrain][];
		System.arraycopy(data, 0, train, 0, numtrain);
		System.arraycopy(data, numtrain, test, 0, data.length - numtrain);
	}

	public void trainAndTest() {
		Layer il, ol;
		// training
		for (int epochn = 0; epochn < epoch; epochn++) {
			for (int dn = 0; dn < train.length; dn++) {
				// set input data
				il = inputLayer;
				il.setOutputs(train[dn]);

				// feed forward
				for (int ln = 1; ln < layers.size(); ln++) {
					ol = layers.get(ln);
					ol.resetInputs();
					il.feedForward(ol);
					ol.transfer();
					il = ol;
				}
				// back propagate
				ol = outputLayer;
				ol.calculateError(target[dn]);
				for (int bln = layers.size() - 2; bln >= 0; bln--) {
					il = layers.get(bln);
					il.backpropagate(ol, learningRate);
					il.calculateError(ol);
					ol = il;
				}
				currentRow = dn;
			}
		}
		totalTraining = epoch * train.length;

		// testing
		int numcorrect = 0;
		for (int dn = 0; dn < test.length; dn++) {
			// set input data
			il = inputLayer;
			il.setOutputs(test[dn]);
			for (int ln = 1; ln < layers.size(); ln++) {
				ol = layers.get(ln);
				ol.resetInputs();
				il.feedForward(ol);
				ol.transfer();
				il = ol;
			}
			currentRow = numtrain + dn;

			double outs[] = outputLayer.getOutputs();
			System.out.println((currentRow + 1) + ":" + Arrays.toString(outs));
			for (int i = 0; i < outs.length; i++) {
				if ((outs[i] > 0.5 ? 1 : 0) != target[numtrain + dn][i]) {
					numcorrect--;
					break;
				}
			}
			numcorrect++;
		}
		double result = Math.round((numcorrect * 10000) / (double) test.length) / 100.0;
		String msg = "Success rate (" + numcorrect + "/" + test.length + "): " + result;
		notifyListeners(msg);
	}
}
