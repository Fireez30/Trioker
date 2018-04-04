package util.aff;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.ConversionCoordonnees;
import util.go.PointVisible;
import util.go.Vecteur;
import util.io.ReadWritePoint;


public class Vue extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
	Color bgColor;
	Color fgColor; 
	int width, height;
	int maxX,minX;
	private ArrayList<PointVisible> points = new ArrayList<PointVisible>();
	private ArrayList<PointVisible> pointstmp = new ArrayList<PointVisible>();
	private ArrayList<Vecteur> aretes = new ArrayList<Vecteur>();
	Point initialLocation, previousLocation, newLocation;
	Rectangle rectangleElastique;
	int initialX, initialY;
	double a,b;

	public Vue(int width, int height, String fileName, boolean modelCoordinates) {
		super();
		Couleur.forPrinter(true);
		this.bgColor = Couleur.bg; 
		this.fgColor = Couleur.fg; 
		this.width = width;
		this.height = height;	
		this.a = 0.5;
		this.b = 0.7;
		this.setBackground(Couleur.bg);
		this.setPreferredSize(new Dimension(width, width));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.initGrille();
		this.pointstmp= points;
		maxX = getMaxX(pointstmp);
		minX = getMinX(pointstmp);
		if(!modelCoordinates)export("trio-hypo-2.csv");
	}



	private void copyModelToViewportCoords() {
		for(PointVisible p: points) {
			p.copyModelToViewportCoords();
		}
	}

	private void initFromLog(String fileName, boolean modelCoordinates) {
		ReadWritePoint rw = new ReadWritePoint(fileName);
		points = rw.read();
		aretes = new ArrayList<Vecteur>();
		int n = points.size();
		for (int i = 0 ; i < n; i++) {
			aretes.add(new Vecteur(points.get(i), points.get((i+1)%n)));

			aretes.add(new Vecteur(points.get(i), points.get((i+2)%n)));

			aretes.add(new Vecteur(points.get(i+1), points.get((i+2)%n)));
		}

	}

	private void initGrille(){
		for (int i = 0; i < points.size(); i++){
			points.get(i).triangle.clear();
		}
		points.clear();
		aretes.clear();
		int cpt=-1;
		for (int i = 50; i < 480 ; i = i + 90){
			for (int j = 50; j < 480 ; j = j + 90){
				PointVisible a = new PointVisible(i,j);
				PointVisible b = new PointVisible(i+30,j+60);
				PointVisible c = new PointVisible(i+60,j);

				a.triangle.add(b);
				a.triangle.add(c);

				b.triangle.add(a);
				b.triangle.add(c);

				c.triangle.add(a);
				c.triangle.add(b);

				points.add(a);

				cpt++;
				aretes.add(new Vecteur(points.get(cpt), b));

				aretes.add(new Vecteur(points.get(cpt), c));

				aretes.add(new Vecteur(b, c));
			}
		}

	}

	public void export(String logFile) {
		ReadWritePoint rw = new ReadWritePoint(logFile);
		for (PointVisible p: points){
			rw.add((int)p.getMC().x+";"+(int)p.getMC().y+";"+p.toString());
		}
		rw.write();
	}

	public void setPoints(ArrayList<PointVisible> points) {
		this.points = points;
	}
	

	public void paintComponent(Graphics g) {
		Stack<Color> c = new Stack<Color>();
		c.push(new Color(0,0,0));
		c.push(new Color(255,0,0));
		c.push(new Color(0,255,0));
		c.push(new Color(0,0,255));
		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaintMode(); 
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);	
		g2d.setColor(fgColor);
		if (rectangleElastique != null) g2d.draw(rectangleElastique);

		for (int i = 0; i < points.size(); i++){
			
			points.get(i).dessineWithoutLabel(g2d);
		}

		for (Vecteur v: aretes) {
			v.dessine(g2d);
		}

		PointVisible p = new PointVisible(height-30,width-40);
		p.setLabel(new Double(a).toString());

		PointVisible p2 = new PointVisible(height-30,width-10);
		p2.setLabel(new Double(b).toString());

		p.dessineWithLabel(g2d);
		p2.dessineWithLabel(g2d);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			RotateTransform(e.getX(),e.getY());
		}
		if	(SwingUtilities.isLeftMouseButton(e)) {
			//SlideTransform(e.getX(),e.getY());
		}

	}
	public void SlideTransform(double newX,double newY) {
		double xfactor = newX - initialX;
		double yfactor = newY - initialY;

		for (int i = 0; i < points.size(); i++){
			if ((points.get(i).getX() > initialX -5 && points.get(i).getX() < initialX + 5) && (points.get(i).getY() > initialY -5 && points.get(i).getY() < initialY + 5)){
				AffineTransform A = new AffineTransform();
				A.translate(xfactor, yfactor);
				ConversionCoordonnees CC = new ConversionCoordonnees(A);
				CC.applyTransf(pointstmp.get(i),points.get(i));
				points.get(i).copyModelToViewportCoords();

				CC.applyTransf(pointstmp.get(i).triangle.get(0),points.get(i).triangle.get(0));
				points.get(i).triangle.get(0).copyModelToViewportCoords();

				CC.applyTransf(pointstmp.get(i).triangle.get(1),points.get(i).triangle.get(1));
				points.get(i).triangle.get(1).copyModelToViewportCoords();
				
				int rayon = 50;
				for (int j = 0; j < points.size(); j++){
					if ((Math.abs(points.get(j).getX() - points.get(i).getX()) < rayon) && (Math.abs(points.get(j).getY() - points.get(i).getY()) < rayon)){
						/*if ((Math.abs(points.get(j).triangle.get(0).getX() - points.get(i).triangle.get(0).getX()) < rayon) && (Math.abs(points.get(j).triangle.get(0).getY() - points.get(i).triangle.get(0).getY()) < rayon)){
							points.get(i).P.x=points.get(j).P.getX();
							points.get(i).P.y=points.get(j).P.getY();
							points.get(i).triangle.get(0).P.x = points.get(j).triangle.get(0).getX();
							points.get(i).triangle.get(0).P.y = points.get(j).triangle.get(0).getY();
							break;
						}
						else if ((Math.abs(points.get(j).triangle.get(1).getX() - points.get(i).triangle.get(1).getX()) < rayon) && (Math.abs(points.get(j).triangle.get(1).getY() - points.get(i).triangle.get(1).getY()) < rayon)){
							points.get(i).P.x=points.get(j).P.getX();
							points.get(i).P.y=points.get(j).P.getY();
							points.get(i).triangle.get(1).P.x = points.get(j).triangle.get(1).getX();
							points.get(i).triangle.get(1).P.y = points.get(j).triangle.get(1).getY();
							break;
						}
						else if ((Math.abs(points.get(j).triangle.get(0).getX() - points.get(i).triangle.get(1).getX()) < rayon) && (Math.abs(points.get(j).triangle.get(0).getY() - points.get(i).triangle.get(1).getY()) < rayon)){
							points.get(i).P.x=points.get(j).P.getX();
							points.get(i).P.y=points.get(j).P.getY();
							points.get(i).triangle.get(0).P.x = points.get(j).triangle.get(1).getX();
							points.get(i).triangle.get(0).P.y = points.get(j).triangle.get(1).getY();
							break;
						}
						else if ((Math.abs(points.get(j).triangle.get(1).getX() - points.get(i).triangle.get(0).getX()) < rayon) && (Math.abs(points.get(j).triangle.get(1).getY() - points.get(i).triangle.get(0).getY()) < rayon)){
							points.get(i).P.x=points.get(j).P.getX();
							points.get(i).P.y=points.get(j).P.getY();
							points.get(i).triangle.get(1).P.x = points.get(j).triangle.get(0).getX();
							points.get(i).triangle.get(1).P.y = points.get(j).triangle.get(0).getY();
							break;
						}*/
						
						points.get(i).P=points.get(j).P;
						points.get(i).triangle.get(0).P = points.get(j).triangle.get(0).P;
						break;
					}

				}
				break;
			}
		}
		

		repaint();
	}

	public void RotateTransform(double newX, double newY){
		for (int i = 0; i < points.size(); i++){
			if ((points.get(i).getX() > initialX -5 && points.get(i).getX() < initialX + 5) && (points.get(i).getY() > initialY -5 && points.get(i).getY() < initialY + 5)){

				AffineTransform transform = new AffineTransform();
				transform.rotate(0.1, points.get(i).getX(), points.get(i).getY());


				ConversionCoordonnees CC = new ConversionCoordonnees(transform);

				CC.applyTransf(pointstmp.get(i).triangle.get(0),points.get(i).triangle.get(0));
				points.get(i).triangle.get(0).copyModelToViewportCoords();

				CC.applyTransf(pointstmp.get(i).triangle.get(1),points.get(i).triangle.get(1));
				points.get(i).triangle.get(1).copyModelToViewportCoords();
				break;
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			for (int i = 0; i > e.getWheelRotation(); i--) {
				a += 0.3;
				b += 0.3;
			}
			repaint();
		}
		else if (e.getWheelRotation() > 0) {
			for (int i = 0; i < e.getWheelRotation(); i++) {
				a -= 0.3;
				b -= 0.3;
			}
			repaint();
		}
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}



	public int getMinX(ArrayList<PointVisible> v){
		int minx = 99999;

		for (int i = 0; i < v.size();i++){
			if (v.get(i).getY() < minx) 
				minx = (int) v.get(i).getY();
		}

		return minx;
	}

	public int getMaxX(ArrayList<PointVisible> v){
		int maxx = 0;

		for (int i = 0; i < v.size();i++){
			if (v.get(i).getX() > maxx) 
				maxx = (int) v.get(i).getX();
		}

		return maxx;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		initialX = e.getX();
		initialY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		SlideTransform(e.getX(),e.getY());
	}
	public double distance(PointVisible p1,PointVisible p2) {
		return Math.sqrt(Math.pow(p2.getX()-p1.getX(),2));
	}

	private void bifocalTransform(int newX, int newY) {


		System.out.println("min :" +minX);
		System.out.println("max :" +maxX);
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getX() < newX && !((points.get(i).getX()-(b/2*distance(points.get(i),new PointVisible(newX,newY)))) < minX)) {
				AffineTransform A = new AffineTransform();
				A.translate(-b/2*distance(points.get(i),new PointVisible(newX,newY)), 0.0);
				ConversionCoordonnees CC = new ConversionCoordonnees(A);
				//CC.applyTransf(points.get(i));
				points.get(i).copyModelToViewportCoords();
			}
			else if (points.get(i).getX() > newX && !((points.get(i).getX()+(b/2*distance(points.get(i),new PointVisible(newX,newY)))) > maxX)) {
				AffineTransform A = new AffineTransform();
				A.translate(b/2*distance(points.get(i),new PointVisible(newX,newY)), 0.0);
				ConversionCoordonnees CC = new ConversionCoordonnees(A);
				//CC.applyTransf(points.get(i));
				points.get(i).copyModelToViewportCoords();
			}

		}
		repaint();
	}

	private void updateElasticRectangle(int newX, int newY) {
		int w = newX - initialLocation.x;
		int h = newY - initialLocation.y;
		previousLocation.x = newX;
		previousLocation.y = newY;		

		rectangleElastique.width = (w >=0)? w: -w;
		rectangleElastique.height = (h >=0)? h: -h;

		if (h < 0) {
			rectangleElastique.y = initialLocation.y +h;
		}

		if (w < 0) {
			rectangleElastique.x = initialLocation.x +w;
		}


		repaint();
	}


	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}


