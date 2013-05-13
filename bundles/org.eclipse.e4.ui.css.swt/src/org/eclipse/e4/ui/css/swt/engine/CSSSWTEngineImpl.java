/*******************************************************************************
 * Copyright (c) 2008, 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *     IBM Corporation
 *******************************************************************************/
package org.eclipse.e4.ui.css.swt.engine;

import java.util.HashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyBackgroundHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyBorderHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyClassificationHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyFontHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyMarginHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyPaddingHandler;
import org.eclipse.e4.ui.css.core.dom.properties.css2.ICSSPropertyTextHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyBackgroundSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyBorderSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyClassificationSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyFontSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyMarginSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyPaddingSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.css2.CSSPropertyTextSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyAlignmentSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyBorderVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyInnerKeylineSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMaximizeVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMaximizedSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMinimizeVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMinimizedSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyMruVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyOuterKeylineSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyShadowVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyShowCloseHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertySimpleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertySingleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyTabHeightHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyTabMarginOffsetHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyTabRendererSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyUnselectedCloseVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyUnselectedImageVisibleSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyUnselectedTabsSWTHandler;
import org.eclipse.e4.ui.css.swt.properties.custom.CSSPropertyWebbyStyleHandler;
import org.eclipse.e4.ui.css.xml.properties.css2.CSSPropertyBackgroundXMLHandler;
import org.eclipse.e4.ui.css.xml.properties.css2.CSSPropertyFontXMLHandler;
import org.eclipse.e4.ui.css.xml.properties.css2.CSSPropertyTextXMLHandler;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * CSS SWT Engine implementation which configure CSSEngineImpl to apply styles
 * to SWT widgets with static handler strategy.
 */
public class CSSSWTEngineImpl extends AbstractCSSSWTEngineImpl {

	private DisposeListener disposeListener;

	public CSSSWTEngineImpl(Display display) {
		super(display);
		init();
	}

	public CSSSWTEngineImpl(Display display, boolean lazyApplyingStyles) {
		super(display, lazyApplyingStyles);
		init();
	}

	private void init() {
		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				handleWidgetDisposed(e.widget);
			}
		};
	}

	protected void initializeCSSPropertyHandlers() {
		
		HashMap handlersMap = propertyHandlerMap;
		String className = this.getClass().getName();
//		if (handlersMap.containsKey(className)) return;
		if (handlersMap.size() != 0) return;
		
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		IExtensionPoint extPoint = registry
				.getExtensionPoint("org.eclipse.e4.ui.css.swt.property.handler");
		for (IExtension e : extPoint.getExtensions()) {
			for (IConfigurationElement ce : e.getConfigurationElements()) {
				if (ce.getName().equals("handler")) {
					String name = ce.getAttribute("composite");
					String adapter = ce.getAttribute("adapter");
//					if (className.equals(adapter)) {
						IConfigurationElement[] children = ce.getChildren();
						String[] names = new String[children.length];
						for (int i = 0; i < children.length; i++) {
							if (children[i].getName().equals("property-name"))
								names[i] =  children[i].getAttribute("name");
						}
						try {
							if (handlersMap.containsKey(adapter)) {
								HashMap adapterMap = (HashMap) handlersMap.get(adapter);
								if (!adapterMap.containsKey(name)){
									Object t = ce.createExecutableExtension("handler");
									for (int i = 0; i < names.length; i++) {									
										adapterMap.put(names[i], t);
									}
								}	
							} else {
								HashMap adaptersMap = new HashMap();
								Object t = ce.createExecutableExtension("handler");
								for (int i = 0; i < names.length; i++) {									
									adaptersMap.put(names[i], t);
								}
								handlersMap.put(adapter, adaptersMap);
							}
							
						} catch (CoreException e1) {
						}
//					}
				}
			}
		}
		//need to be craeful of the case where the property we are looking for is actually in a parent class
		//that hasn't been initialized
		//if no property exists for this one, add the element to the map to avoid future lookups
//		if (!handlersMap.containsKey(className))  {
//			handlersMap.put(className, null);
//		}
		
		if (true) return;
		// Register SWT CSS Property Background Handler
		super.registerCSSPropertyHandler(ICSSPropertyBackgroundHandler.class,
				CSSPropertyBackgroundSWTHandler.INSTANCE);
		// Register SWT CSS Property Border Handler
		super.registerCSSPropertyHandler(ICSSPropertyBorderHandler.class,
				CSSPropertyBorderSWTHandler.INSTANCE);
		// Register SWT CSS Property Classification Handler
		super.registerCSSPropertyHandler(
				ICSSPropertyClassificationHandler.class,
				CSSPropertyClassificationSWTHandler.INSTANCE);
		// Register SWT CSS Property Text Handler
		super.registerCSSPropertyHandler(ICSSPropertyTextHandler.class,
				CSSPropertyTextSWTHandler.INSTANCE);
		// Register SWT CSS Property Font Handler
		super.registerCSSPropertyHandler(ICSSPropertyFontHandler.class,
				CSSPropertyFontSWTHandler.INSTANCE);
		// Register SWT CSS Property Margin Handler
		super.registerCSSPropertyHandler(ICSSPropertyMarginHandler.class,
				CSSPropertyMarginSWTHandler.INSTANCE);
		// Register SWT CSS Property Padding Handler
		super.registerCSSPropertyHandler(ICSSPropertyPaddingHandler.class,
				CSSPropertyPaddingSWTHandler.INSTANCE);

		// Register XML CSS Property Background Handler
		super.registerCSSPropertyHandler(ICSSPropertyBackgroundHandler.class,
				CSSPropertyBackgroundXMLHandler.INSTANCE);
		// Register XML CSS Property Text Handler
		super.registerCSSPropertyHandler(ICSSPropertyTextHandler.class,
				CSSPropertyTextXMLHandler.INSTANCE);
		// Register XML CSS Property Font Handler
		super.registerCSSPropertyHandler(ICSSPropertyFontHandler.class,
				CSSPropertyFontXMLHandler.INSTANCE);
		
		//CTabFolder properties
		
		//Register SWT CSS Property BorderVisible
		super.registerCSSProperty("border-visible", CSSPropertyBorderVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyBorderVisibleSWTHandler.class,
				CSSPropertyBorderVisibleSWTHandler.INSTANCE);
		//Register SWT CSS Property Simple
		super.registerCSSProperty("simple", CSSPropertySimpleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertySimpleSWTHandler.class,
				CSSPropertySimpleSWTHandler.INSTANCE);
		//Register SWT CSS Property MaximizeVisible
		super.registerCSSProperty("maximize-visible", CSSPropertyMaximizeVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyMaximizeVisibleSWTHandler.class,
				CSSPropertyMaximizeVisibleSWTHandler.INSTANCE);
		//Register SWT CSS Property MinimizeVisible
		super.registerCSSProperty("minimize-visible", CSSPropertyMinimizeVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyMinimizeVisibleSWTHandler.class,
				CSSPropertyMinimizeVisibleSWTHandler.INSTANCE);
		//Register SWT CSS Property mruVisible
		super.registerCSSProperty("mru-visible", CSSPropertyMruVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyMruVisibleSWTHandler.class,
				CSSPropertyMruVisibleSWTHandler.INSTANCE);
		//Register SWT CSS Property Maximized
		super.registerCSSProperty("maximized", CSSPropertyMaximizedSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyMaximizedSWTHandler.class,
				CSSPropertyMaximizedSWTHandler.INSTANCE);
		//Register SWT CSS Property Minimized
		super.registerCSSProperty("minimized", CSSPropertyMinimizedSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyMinimizedSWTHandler.class,
				CSSPropertyMinimizedSWTHandler.INSTANCE);
		//Register SWT CSS Property Single
		super.registerCSSProperty("single", CSSPropertySingleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertySingleSWTHandler.class,
				CSSPropertySingleSWTHandler.INSTANCE);
		//Register SWT CSS Property UnselectedCloseVisible
		super.registerCSSProperty("unselected-close-visible", CSSPropertyUnselectedCloseVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyUnselectedCloseVisibleSWTHandler.class,
				CSSPropertyUnselectedCloseVisibleSWTHandler.INSTANCE);
		//Register SWT CSS Property UnselectedImageVisible
		super.registerCSSProperty("unselected-image-visible", CSSPropertyUnselectedImageVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyUnselectedImageVisibleSWTHandler.class,
				CSSPropertyUnselectedImageVisibleSWTHandler.INSTANCE);
		//Register CTabFolder CSS Property tab-height
		super.registerCSSProperty("tab-height", CSSPropertyTabHeightHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyTabHeightHandler.class,
				CSSPropertyTabHeightHandler.INSTANCE);

		//Register CTabItem CSS Property show-close
		super.registerCSSProperty("show-close", CSSPropertyShowCloseHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyShowCloseHandler.class,
				CSSPropertyShowCloseHandler.INSTANCE);

		//Register SWT CSS Property Alignment
		super.registerCSSProperty("alignment", CSSPropertyAlignmentSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyAlignmentSWTHandler.class,
				CSSPropertyAlignmentSWTHandler.INSTANCE);
		
		//Register SWT CSS Property UnselectedTabsColor
		super.registerCSSProperty("unselected-tabs-color", CSSPropertyUnselectedTabsSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyUnselectedTabsSWTHandler.class,
				CSSPropertyUnselectedTabsSWTHandler.INSTANCE);
		
		//Register SWT CSS Property tab renderer
		super.registerCSSProperty("tab-renderer", CSSPropertyTabRendererSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyTabRendererSWTHandler.class,
				CSSPropertyTabRendererSWTHandler.INSTANCE);
		
		//Register SWT CSS Property corner radius
//		super.registerCSSProperty("corner-radius", CSSPropertyCornerRadiusSWTHandler.class);  
//		super.registerCSSPropertyHandler(CSSPropertyCornerRadiusSWTHandler.class,
//				CSSPropertyCornerRadiusSWTHandler.INSTANCE);
		
	
		
		//Register SWT CSS Property shadow visible
		super.registerCSSProperty("shadow-visible", CSSPropertyShadowVisibleSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyShadowVisibleSWTHandler.class,
				CSSPropertyShadowVisibleSWTHandler.INSTANCE);
		
		//Register SWT CSS Property outer keyline color
		super.registerCSSProperty("outer-keyline-color", CSSPropertyOuterKeylineSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyOuterKeylineSWTHandler.class,
				CSSPropertyOuterKeylineSWTHandler.INSTANCE);
		
		//Register SWT CSS Property inner keyline color
		super.registerCSSProperty("inner-keyline-color", CSSPropertyInnerKeylineSWTHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyInnerKeylineSWTHandler.class,
				CSSPropertyInnerKeylineSWTHandler.INSTANCE);
		
		//ETabFolder properties
		
		//Register ETabFolder CSS Property webby-style
		super.registerCSSProperty("webby-style", CSSPropertyWebbyStyleHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyWebbyStyleHandler.class,
				CSSPropertyWebbyStyleHandler.INSTANCE);

		//Register ETabFolder CSS Property tab-margin-offset
		super.registerCSSProperty("tab-margin-offset", CSSPropertyTabMarginOffsetHandler.class);  
		super.registerCSSPropertyHandler(CSSPropertyTabMarginOffsetHandler.class,
				CSSPropertyTabMarginOffsetHandler.INSTANCE);
	}
	
	@Override
	protected void hookNativeWidget(Object widget) {
		Widget swtWidget = (Widget) widget;
		swtWidget.addDisposeListener(disposeListener);
	}

}
