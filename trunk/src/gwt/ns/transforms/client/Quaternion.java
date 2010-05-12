/*
 * Copyright 2010 Brendan Kenny
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwt.ns.transforms.client;

// TODO: fix api. confusing
public class Quaternion {
	
	public static void copy(Quaternion dest, Quaternion src) {
		dest.x = src.x;
		dest.y = src.y;
		dest.z = src.z;
		dest.w = src.w;
	}
	
	public static void multiply(Quaternion a, Quaternion b, Quaternion dest) {
		double tx = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;
		double ty = a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z;
		double tz = a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x;
		
		double tw = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
		
		dest.x = tx;
		dest.y = ty;
		dest.z = tz;
		dest.w = tw;
	}
	
	public double x = 0.;
	public double y = 0.;
	public double z = 0.;
	public double w = 1.;
	
	/**
	 * Create an unit identity quaternion.
	 */
	public Quaternion() {
		
	}
	
	public Quaternion(double x, double y, double z, double w) {
		set(x, y, z, w);
	}
	
	/**
	 * 
	 * @param target
	 */
	public void convertToMatrix(Matrix4x4 target) {
		// unclear that this really saves anything
		double xx = x*x, xy = x*y, xz = x*z, xw = x*w;
		double yy = y*y, yz = y*z, yw = y*w;
		double zz = z*z, zw = z*w;
		
		// normalize for them...(no?)
		normalize();
		
		target.m11 = 1 - 2 * (yy + zz);
		target.m12 = 2 * (xy - zw);
		target.m13 = 2 * (xz + yw);
		target.m14 = 0;
		
		target.m21 = 2 *(xy + zw);
		target.m22 = 1 - 2 * (xx + zz);
		target.m23 = 2 * (yz - xw);
		target.m24 = 0;
		
		target.m31 = 2 * (xz - yw);
		target.m32 = 2 * (yz + xw);
		target.m33 = 1 - 2 * (xx + yy);
		target.m34 = 0;
		
		target.m41 = 0;
		target.m42 = 0;
		target.m43 = 0;
		target.m44 = 1;
	}
	
	public double norm() {
		return Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	public void normalize() {
		// careful!
		double invMag = norm();
		assert invMag != 0 : "Cannot normalize a zero norm quaternion.";
			
		invMag = 1. / invMag;
		x *= invMag;
		y *= invMag;
		z *= invMag;
		w *= invMag;
	}
	
	public void negate() {
		x *= -1;
		y *= -1;
		z *= -1;
		w *= -1;
	}
	
	public void set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		
		normalize();
	}
	
	public void setIdentity() {
		set(0., 0., 0., 1.);
	}
}
