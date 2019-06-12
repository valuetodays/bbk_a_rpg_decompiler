package com.billy.bbk.arpg.compiler.gut;

/**
 * @author lei.liu
 * @since 2019-06-12 13:51
 */
public class GutCommand {

    private final String name;
    private final String param;

    public GutCommand(String name, String param) {
        this.name = name;
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public String getParam() {
        return param;
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "name='" + name + '\'' +
                ", param='" + param + '\'' +
                '}';
    }

}
