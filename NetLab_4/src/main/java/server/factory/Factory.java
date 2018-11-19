package server.factory;

import server.AbstractRestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

public class Factory
{
	private static TreeMap<String, Class> handlers = null;
    private static Factory instance;
	private static Properties properties = null;
	
	private Factory() throws FactoryException //
    {
        handlers = new TreeMap<String, Class>();
        try {
            properties = new Properties();
            InputStream is = getClass().getResourceAsStream("properties.txt");
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
            handler = (AbstractRestHandler) c.newInstance();
        }
        catch(IllegalAccessException | InstantiationException e) {
            throw new FactoryException("IllegalAccessException or InstantiationException");
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


    /*private void fabric()
    {
        Reader reader = null;
        reader = new InputStreamReader(Factory.class.getResourceAsStream("1.txt"));
        System.out.println();
    }*/
}
