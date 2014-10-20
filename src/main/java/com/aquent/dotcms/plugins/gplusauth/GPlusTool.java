package com.aquent.dotcms.plugins.gplusauth;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.util.Logger;

/**
 * 
 * @author Aquent, LLC (cfalzone@aquent.com)
 *
 */
public class GPlusTool implements ViewTool {

	@Override
	public void init(Object initData) {
		Logger.info(this, "GPlus Viewtool Initialized");
	}

}
