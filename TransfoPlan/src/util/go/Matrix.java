package util.go;

import java.util.Vector;

public class Matrix {
	Vector<Vector<Float>> m;
	int sizeH;
	int sizeW;
	
	public Matrix(){
		m = new Vector<Vector<Float>>();
		sizeH = 0;
		sizeW = 0;
	}
	
	public Matrix(int sH, int sW){
		m = new Vector<Vector<Float>>();
		for (int i = 0; i < sH ; i++){
			m.addElement(new Vector<Float>());
			for (int j = 0; j < sW; j++){
				m.get(i).addElement(1f);
			}
		}
		sizeH = sH;
		sizeW = sW;
	}

	public Vector<Vector<Float>> getM() {
		return m;
	}

	public void setM(Vector<Vector<Float>> m) {
		this.m = m;
	}

	public int getSizeH() {
		return sizeH;
	}

	public void setSizeH(int sizeH) {
		this.sizeH = sizeH;
	}

	public int getSizeW() {
		return sizeW;
	}

	public void setSizeW(int sizeW) {
		this.sizeW = sizeW;
	}
	public void addElement(int x, int y, float value){
		m.get(x).set(y,value);
	}
	
	@Override
	public String toString() {
		return "Matrix [m=" + m + ", sizeH=" + sizeH + ", sizeW=" + sizeW + "]";
	}
	
}
