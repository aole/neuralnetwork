package aole;

import java.util.ArrayList;
import java.util.Random;

public class Node {

	ArrayList<Node> outNodes = new ArrayList<>();
	ArrayList<Double> weights = new ArrayList<>();
	int locx, locy;
	double output = 1;
	double input;
	double derivative;
	boolean isBias;

	public Node(Layer inputlayer) {
		this(inputlayer, false);
	}

	public Node(Layer inputlayer, boolean b) {
		isBias = b;
		Random random = new Random();

		if (inputlayer != null) {
			for (Node n : inputlayer.nodes) {
				if (n.isBias)
					continue;
				outNodes.add(n);
				weights.add(random.nextDouble());
			}
		}
	}

	public void setLocation(int x, int y) {
		locx = x;
		locy = y;
	}
}
