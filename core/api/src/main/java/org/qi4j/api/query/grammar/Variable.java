package org.qi4j.api.query.grammar;

/**
 * Query Variable name.
 */
public class Variable
{
    String name;

    public Variable( String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
