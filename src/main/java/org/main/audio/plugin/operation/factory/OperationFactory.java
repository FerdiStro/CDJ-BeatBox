package org.main.audio.plugin.operation.factory;

import org.main.audio.plugin.operation.None;
import org.main.audio.plugin.operation.Operation;
import org.main.util.Logger;
import org.reflections.Reflections;

import java.util.Set;

public  class OperationFactory {

    private String packageName = "org.main.audio.plugin.operation";


    public OperationFactory setPackageName(String packageName){
        this.packageName  = packageName;
        return this;
    }

    public static OperationFactory newBuilder(){
        return new OperationFactory();
    }

    private String name;

    public OperationFactory setName(String name) {
        this.name = name;
        return this;
    }

    private double value;

    public OperationFactory setValue(double value) {
        this.value = value;
        return this;
    }


    public Operation build(){
        if(name ==  null || name.isEmpty()){
            return new None();
        }
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Operation>> subTypes = reflections.getSubTypesOf(Operation.class);

        for (Class<? extends Operation> clazz : subTypes) {
            try {
                Operation operation = clazz.getDeclaredConstructor().newInstance();
                if (operation.getName().equalsIgnoreCase(name)) {
                    operation.setValue(value);
                    return operation;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.debug("No Operation found for " + name + " in " + packageName);
        return new None();
    }



    private OperationFactory(){}


}
