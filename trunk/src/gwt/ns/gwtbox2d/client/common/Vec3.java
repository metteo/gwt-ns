/*
 * This file has been modified from the original JBox2D source.
 * Original source license found below.
 * 
 * Modifications Copyright 2009 Brendan Kenny
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

/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package gwt.ns.gwtbox2d.client.common;

// created from build 218
public class Vec3 {
	public float x,y,z;
	
	public Vec3(){
		x=y=z=0f;
	}
	
	public Vec3(float argX, float argY, float argZ){
		x = argX;
		y = argY;
		z = argZ;
	}
	
	public Vec3(Vec3 argCopy){
		x = argCopy.x;
		y = argCopy.y;
		z = argCopy.z;
	}
	
	public void set(Vec3 argVec){
		x = argVec.x;
		y = argVec.y;
		z = argVec.z;
	}
	
	public Vec3 addLocal(Vec3 argVec){
		x += argVec.x;
		y += argVec.y;
		z += argVec.z;
		return this;
	}
	
	public Vec3 add(Vec3 argVec){
		return new Vec3(x + argVec.x,
		                y + argVec.y,
		                z + argVec.z);
	}
	
	public Vec3 subLocal(Vec3 argVec){
		x -= argVec.x;
		y -= argVec.y;
		z -= argVec.z;
		return this;
	}
	
	public Vec3 sub(Vec3 argVec){
		return new Vec3(x - argVec.x,
		                y - argVec.y,
		                z - argVec.z);
	}
	
	public Vec3 mulLocal(float argScalar){
		x *= argScalar;
		y *= argScalar;
		z *= argScalar;
		return this;
	}
	
	public Vec3 mul(float argScalar){
		return new Vec3(x * argScalar,
		                y * argScalar,
		                z * argScalar);
	}
	
	public Vec3 negate(){
		return new Vec3(-x,-y,-z);
	}
	
	public Vec3 negateLocal(){
		x*=-1;
		y*=-1;
		z*=-1;
		return this;
	}
	
	public void setZero(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vec3 clone(){
		return new Vec3(this);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + Float.floatToIntBits(x);
		//result = prime * result + Float.floatToIntBits(y);
		//result = prime * result + Float.floatToIntBits(z);
		//change for gwt
		result = prime * result + Float.valueOf(x).hashCode();
		result = prime * result + Float.valueOf(y).hashCode();
		result = prime * result + Float.valueOf(z).hashCode();
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec3 other = (Vec3) obj;
		//if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
		//	return false;
		//if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
		//	return false;
		//if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
		//	return false;
		//the above seems very strange, but i don't know the intentions of the
		//author. regardless, need to change for gwt
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
	
	public final static float dot(Vec3 a, Vec3 b){
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	public final static Vec3 cross(Vec3 a, Vec3 b){
		return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}
	
	public final static void crossToOut(Vec3 a, Vec3 b, Vec3 out){
		final float tempy = a.z * b.x - a.x * b.z;
		final float tempz = a.x * b.y - a.y * b.x;
		out.x = a.y * b.z - a.z * b.y;
		out.y = tempy;
		out.z = tempz;
	}
}
