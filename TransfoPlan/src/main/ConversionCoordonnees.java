package main;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;

import util.go.PointVisible;

public class ConversionCoordonnees {
	
		AffineTransform m;
		
		public ConversionCoordonnees(){
			m = new AffineTransform();// matrice identite
		}
		
		public ConversionCoordonnees(AffineTransform A){
			m = new AffineTransform(A);// matrice identite
		}
		
		
		public void applyTransf(PointVisible o,PointVisible p){
				m.transform(o.P, p.P);
		}
		
}
