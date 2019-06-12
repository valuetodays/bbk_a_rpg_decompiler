package com.billy.bbk.arpg.compiler.gut;

/**
 * @author lei.liu
 * @since 2019-06-12 13:51
 */
class GutCommand {

    private final String name;
    private final String param;

    GutCommand(String name, String param) {
        this.name = name;
        this.param = param;
    }

    String getName() {
        return name;
    }

    String getParam() {
        return param;
    }

}
