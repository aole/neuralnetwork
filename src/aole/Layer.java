package aole;

import java.util.ArrayList;

public class Layer {

	public static final int INPUT = 1;
	public static final int HIDDEN = 2;
	public static final int OUTPUT = 3;

	ArrayList<Node> nodes = new ArrayList<>();
	int type;

	public Layer(int type, int num) {
		this(type, num, (Layer) null);
	}

	public Layer(int type, int num, Layer outputlayer) {
		this(type, num, outputlayer, null);
	}

	public Layer(int type, int num, Layer outputlayer, String[] targetLabel) {
		this.type = type;
		for (int i = 0; i < num; i++) {
			Node n;
			if (targetLabel != null)
				n = new Node(outputlayer, false, targetLabel[i]);
			else
				n = new Node(outputlayer);
			nodes.add(n);
		}
		// add a bias, except in case of an output layer
		if (type != OUTPUT) {
			nodes.add(new Node(outputlayer, true));
		}
	}

	public int getNumNodes() {
		return nodes.size();
	}

	public void setOutputs(double[] data) {
		for (int nn = 0; nn < nodes.size(); nn++) {
			Node n = nodes.get(nn);
			if (!n.isBias)
				n.output = data[nn];
		}
	}

	public void feedForward(Layer outputlayer) {
		for (Node n : nodes) {
			for (int onn = 0; onn < outputlayer.nodes.size(); onn++) {
				Node on = outputlayer.nodes.get(onn);
				if (on.isBias)
					continue;
				double weight = n.weights.get(onn);
				on.input += n.output * weight;
			}
		}
	}

	public void resetInputs() {
		for (Node n : nodes) {
			if (!n.isBias)
				n.input = 0;
		}
	}

	public void transfer() {
		for (Node n : nodes) {
			if (n.isBias)
				continue;
			n.output = 1.0 / (1.0 + Math.exp(-n.input));
		}
	}

	public double calculateError(int target[]) {
		double globalerror = 0;
		for (int nn = 0; nn < nodes.size(); nn++) {
			Node n = nodes.get(nn);
			double error = target[nn] - n.output;
			n.derivative = error * n.output * (1.0 - n.output);
			globalerror += Math.abs(error);
		}
		return globalerror;
	}

	public void calculateError(Layer outputlayer) {
		for (Node n : nodes) {
			n.derivative = 0;
			for (int onn = 0; onn < outputlayer.nodes.size(); onn++) {
				Node on = outputlayer.nodes.get(onn);
				if (on.isBias)
					continue;
				double w = n.weights.get(onn);
				n.derivative += n.output * (1.0 - n.output) * w * on.derivative;
			}
		}
	}

	public void backpropagate(Layer outputlayer, double rate) {
		for (Node n : nodes) {
			for (int onn = 0; onn < outputlayer.nodes.size(); onn++) {
				Node on = outputlayer.nodes.get(onn);
				if (on.isBias)
					continue;
				double w = n.weights.get(onn);
				w += rate * n.output * on.derivative;
				n.weights.set(onn, w);
			}
		}
	}
}
