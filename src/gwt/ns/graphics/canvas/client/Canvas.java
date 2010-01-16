/*
 * Copyright 2010 Brendan Kenny
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package gwt.ns.graphics.canvas.client;

import gwt.ns.graphics.canvas.client.impl.CanvasElementFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that displays a canvas or a canvas emulated by the excanvas
 * javascript library. 
 * 
 * @see <a href='http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html'>HTML5 Draft Standard for Canvas</a>
 *  @see <a href='http://code.google.com/p/explorercanvas/'>Explorercanvas project</a>
 */
public class Canvas extends Widget {
  static final CanvasElementFactory canvasFactory = (CanvasElementFactory)GWT.create(CanvasElementFactory.class);
  private Element canvasElement;  
  
  /**
   * Creates a Canvas Widget.
   * 
   * <p>
   * Intrinsic size of canvas defaults to <b>300x150</b> pixels.
   * </p>
   */
  public Canvas() {
    this(300, 150);
  }

  /**
   * Creates a Canvas Widget
   * <p>
   * Size specified is the Canvas's intrinsic size. The coordinate system
   * used for drawing is based on this size (until transformations are
   * applied).
   * </p>
   * 
   * @param width The intrinsic width of the canvas
   * @param height The intrinsic height of the canvas
   */
  public Canvas(int width, int height) {
    canvasElement = canvasFactory.createCanvas(width, height);
    setElement(canvasElement);
    //setWidth(width);
    //setHeight(height);
    
    // don't like to do this, but need to for excanvas
    // TODO: spin off with deferred binding or make more transparent to user
    // maybe factory method should take size?
    //Style style = canvasElement.getStyle();
    //style.setWidth(width, Unit.PX);
    //style.setHeight(height, Unit.PX);
  }
  
  /**
   * Returns an object that exposes a 2D API for drawing on the canvas.
   * Test for same Canvas context by {@link CanvasContext2d#}
   * 
   * @return 2d Canvas Context
   */
  public CanvasContext2d getContext2d() {
    // could make a singleton, but no need, really. canvas element always
    // returns same context object and java object adds minimal overhead.
    JavaScriptObject nativeContext = nativeGetContext();
    CanvasContext2d context = new CanvasContext2d();//GWT.create(CanvasContext2d.class);
    context.init(this, nativeContext);
    return context;
  }
  
  /**
   * Retrieve the native javascript canvas context. Since excanvas is already
   * initialized, this method will return for all supported user agents
   * 
   * @return JavaScriptObject reference to native canvas context.
   */
  private native JavaScriptObject nativeGetContext() /*-{
    return (this.@gwt.ns.graphics.canvas.client.Canvas::canvasElement).getContext('2d');
  }-*/;
  
  /**
   * Convenience function for resizing the canvas.
   * 
   * @param width The width of the canvas in pixels
   * @param height The height of the canvas in pixels
   */
  public void resize(int width, int height) {
    setHeight(height);
    setWidth(width);
  }


  /**
   * Sets the intrinsic height of the canvas in pixels. Resets the content of
   * the Canvas.
   * 
   * @param height The height of the canvas in pixels
   */
  public void setHeight(int height) {
    getElement().setAttribute("height", height + "");
  }

  /**
   * Sets the intrinsic width of the canvas in pixels. Resets the content of
   * the Canvas.
   * 
   * @param width The width of the canvas in pixels
   */
  public void setWidth(int width) {
    getElement().setAttribute("width", width + "");
  }
  
  /**
   * Returns a data URL for the canvas represented as a PNG file. Currently
   * unsupported in Internet Explorer.
   *
   * @return A data URL for the image in the canvas if supported on current
   *   platform, null if not.
   */
  public native String toDataUrl() /*-{
    // TODO: perhaps some expressiveness so platforms that do not support it
    // can see why at runtime (on IE, etc)
    var cel = this.@gwt.ns.graphics.canvas.client.Canvas::canvasElement;
    if (typeof cel.toDataURL === 'function') {
      return cel.toDataURL();
    } else {
      return null;	// also null in java-land
    }
  }-*/;
}
