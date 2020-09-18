package com.lexer;

public class Token
{
    private String type;
    private String value;
    private Integer lineNumber;


    public Token(String type, String value)
    {
        initialize(type, value);
    }

    //setters
    public void setType(String type)
    {
        this.type = type;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public void setLineNumber(Integer n)
    {
        this.lineNumber = n;
    }
    //getters
    public String getType()
    {
        return this.type;
    }
    public String getValue()
    {
        return this.value;
    }
    public Integer getLineNumber()
    {
        return this.lineNumber;
    }

    public void incrementLineNumber()
    {
        this.lineNumber++;
    }

    @Override
    public String toString()
    {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean equals(Token token)
    {
        if(token.getType().equals(this.type) && token.getValue().equals(this.value))
        {
            return true;
        }
        return false;
    }

    //private methods
    private void initialize(String type, String value)
    {
        setType(type);
        setValue(value);
    }
}
