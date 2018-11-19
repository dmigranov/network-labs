package server.factory;

import server.Messages;
import server.Users;
import server.handlers.AbstractRestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.TreeMap;

public class Factory
{
	private static TreeMap<String, Class> handlers = null;
    private static Factory instance;
	private static Properties properties = null;
	private static Users users;
    private static Messages messages;
	
	private Factory() throws FactoryException
    {
        handlers = new TreeMap<>();
        try {
            properties = new Properties();
            InputStream is = getClass().getResourceAsStream("properties.txt");
            //InputStream is = Factory.class.getClassLoader().getResourceAsStream("properties.txt");
            if(is != null)
                properties.load(is);
            else throw new IOException();
        }
        catch(IOException e)
        {
            //e.printStackTrace(System.err);
            throw new FactoryException("Problem reading the properties file, impossible to initialise a fabric");
        }
    }

    public static Factory getInstance() throws FactoryException
    {
        if (instance == null)
            instance = new Factory();
        return instance;
    }

    public AbstractRestHandler getHandler(String name) throws FactoryException
    {
        AbstractRestHandler handler;
        //String s = properties.getProperty(name);
        if(!handlers.containsKey(name)) {
            registerHandler(name);
        }
        Class c = handlers.get(name);
        if(c == null)
            throw new FactoryException("Handler doesn't exist");
        try {
            //handler = (AbstractRestHandler) c.newInstance();
            handler = (AbstractRestHandler)c.getDeclaredConstructor(Users.class, Messages.class).newInstance(users, messages);
        }
        catch(IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            throw new FactoryException("IllegalAccessException or InstantiationException");
            //throw new FactoryException(e.toString());
        }

        return handler;
    }

    private void registerHandler(String name) throws FactoryException
    {
        String s = properties.getProperty(name);
        if (s==null)
        {
            throw new FactoryException("There is no such handler, check the properties file");
        }
        try {
            handlers.put(name, Class.forName(s));
        }
        catch(ClassNotFoundException e) {
            throw new FactoryException("There is no such class, check the properties file");
        }

    }

    public void init(Users users, Messages messages) {
	    this.users = users;
	    this.messages = messages;
    }
}
