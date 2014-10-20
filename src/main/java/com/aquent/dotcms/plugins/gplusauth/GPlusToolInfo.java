package com.aquent.dotcms.plugins.gplusauth;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

/**
 * 
 * @author Aquent, LLC (cfalzone@aquent.com)
 *
 */
public class GPlusToolInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "gplus";
    }

    @Override
    public String getScope () {
        return ViewContext.APPLICATION;
    }

    @Override
    public String getClassname () {
        return GPlusTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        GPlusTool viewTool = new GPlusTool();
        viewTool.init( initData );

        setScope( ViewContext.APPLICATION );

        return viewTool;
    }

}
