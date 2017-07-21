package aole;

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

	double data[][];
	int target[][];
	String labels[];
	double learningRate = .2;
	int totalTraining = 0;

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

	private void notifyListeners(int epoch, String msg, double error) {
		for (NetworkListener nl : listeners) {
			nl.networkUpdated(epoch, msg, error);
		}
	}

	public void trainTo(int count, int updateEvery) {
		Layer il, ol;
		double error = 0;
		int dn = 0;
		int finalcount = count + totalTraining;
		for (; totalTraining < finalcount; totalTraining++) {
			dn = totalTraining % data.length;
			// set input data
			il = inputLayer;// layers.get(0);
			il.setOutputs(data[dn]);
			// System.out.println(Arrays.toString(data[dn]) + " = " +
			// Arrays.toString(target[dn]));
			// feed forward
			for (int ln = 1; ln < layers.size(); ln++) {
				ol = layers.get(ln);
				ol.resetInputs();
				il.feedForward(ol);
				ol.transfer();
				il = ol;
			}
			// back propagate
			ol = outputLayer; // layers.get(layers.size() - 1);
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
				notifyListeners(totalTraining,
						"(" + totalTraining + ") " + Arrays.toString(data[dn]) + " = " + Arrays.toString(target[dn]),
						error);
			}
		}
		// notifyListeners(totalTraining, Arrays.toString(data[dn]) + " = " +
		// Arrays.toString(target[dn]), error);
	}

	public CategoryDataset getErrorDataset() {
		return errors;
	}

	public void resetWeights() {
		Random random = new Random();
		totalTraining = 0;
		
		for (Layer l : layers) {
			for (Node n : l.nodes) {
				for (int i = 0; i < n.weights.size(); i++) {
					n.weights.set(i, random.nextDouble());
				}
			}
		}
	}

}
