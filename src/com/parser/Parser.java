package com.parser;

import com.interpreter.ByteCodeInterpreter;
import com.lexer.Lexer;
import com.lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser
{
    int index = 0;
    private Integer errorLineNumber;
    private ArrayList<Token> tokenList;
    private SymbolTable symbolTable;
    private boolean parsingExpression = false;
    private Token tokenToStore; //used to indicate which ID token will receive the STORE operation at its specified address

    private static final String EXPECTID = "Expecting Identifier";
    private static final String EXPECTASSIGNOP = "Expecting Assignment Operator";
    private static final String EXPECTIDORINT = "Expecting Identifier or Integer";
    private static final String EXPECTIDORPLUSOP = "Expecting Identifier or Add Operator";
    private static final String NOTDEFINED = "Identifier not defined";

    private static final String INTTOKEN="INT";
    private static final String IDTOKEN="ID";
    private static final String ASSMTTOKEN="ASSMT";
    private static final String PLUSTOKEN="PLUS";
    private static final String EOFTOKEN="EOF";
    private static final String UNKNOWNTOKEN="UNKNOWN";

    private static final Integer LOAD = 0;
    private static final Integer LOADI = 1;
    private static final Integer STORE = 2;
    private ByteCodeInterpreter bci;

    public Parser(String fileName)
    {
        initialize(fileName);
    }

    public SymbolTable getSymbolTable()
    {
        return this.symbolTable;
    }

    public void setErrorLineNumber(Integer n)
    {
        this.errorLineNumber = n;
    }

    public Integer getErrorLineNumber()
    {
        return this.errorLineNumber;
    }

    @Override
    public String toString()
    {
        return "Parser{" +
                "index=" + index +
                ", errorLineNumber=" + errorLineNumber +
                ", tokenList=" + tokenList.toString() +
                ", symbolTable=" + symbolTable.toString() +
                ", parsingExpression=" + parsingExpression +
                ", tokenToStore=" + tokenToStore.toString() +
                ", bci=" + bci.toString() +
                '}';
    }

    public boolean equals(Parser parser)
    {
        if(!this.symbolTable.equals(parser.getSymbolTable()))
        {
            System.out.println("symbool");
            return false;
        }
        if(!this.bci.equals(parser.getByteInterpreter()))
        {
            System.out.println("bci");
            return false;
        }
        return true;
    }

    public ArrayList<Token> getTokenList()
    {
        return this.tokenList;
    }

    public ByteCodeInterpreter getByteInterpreter()
    {
        return this.bci;
    }
    public void parseProgram() throws Exception
    {
        boolean isValid = true;
        symbolTable = new SymbolTable();
        this.bci = new ByteCodeInterpreter(10, this.symbolTable); //this constructor sets all data members of the object and also calls the generate() method.
        while(isValid)
        {
            isValid = parseAssignment();
        }
        //instantiate ByteCodeInterpreter object
        this.bci.run(); //runs the bytecode
    }

    private boolean parseAssignment() throws Exception
    {
        boolean isValid = false;
        Token token = tokenList.get(index);
        setErrorLineNumber(token.getLineNumber());
        if(token.getType().equals(EOFTOKEN))
        {
            return false;
        }
        determineParsingExpression(token);
        if(parsingExpression)
        {
            isValid = parseExpression(token);
            return isValid;
        }
        if(token.getType().equals(IDTOKEN))
        {
            isValid = parseId(token);
        }
        else if(token.getType().equals(INTTOKEN))
        {
            isValid = parseInt(token);
        }
        else if(token.getType().equals(PLUSTOKEN))
        {
            isValid = parsePlusOp();
        }
        else if(token.getType().equals(ASSMTTOKEN))
        {
            isValid = parseAssmt();
        }
        return isValid;
    }

    private boolean parseExpression(Token token) throws Exception
    {
        boolean isValid = false;
        if(token.getType().equals(IDTOKEN))
        {
            isValid = parseId(token);
        }
        else if(token.getType().equals(INTTOKEN))
        {
            isValid = parseInt(token);
        }
        else if(token.getType().equals(PLUSTOKEN))
        {
            isValid = parsePlusOp();
        }
        else if(token.getType().equals(ASSMTTOKEN))
        {
            isValid = parseAssmt();
        }
        return isValid;
    }

    private boolean parseId(Token token) throws Exception
    {
        boolean isValid = true;
        boolean idExists = symbolTable.isInTable(token);
        Token nextToken = nextToken();

        if(!parsingExpression)
        {
            if(!idExists)
            {
                symbolTable.addIdentifier(token.getValue());
            }
            if(nextToken.getType().equals(INTTOKEN) || nextToken.getType().equals(IDTOKEN) || nextToken.getType().equals(UNKNOWNTOKEN) || nextToken.getType().equals(PLUSTOKEN) || nextToken.getType().equals(EOFTOKEN))
            {
                throw new Exception(EXPECTASSIGNOP + " in line " + this.errorLineNumber);
            }
            setTokenToStore(token); //sets the data member tokenToStore, b/c and ID before an ASSMT will need to be stored at the end of the expression.
        }
        if(parsingExpression)
        {
            if(!idExists)
            {
                throw new Exception(NOTDEFINED + " in line " + this.errorLineNumber);
            }
            if(nextToken.getType().equals(INTTOKEN) || nextToken.getType().equals(UNKNOWNTOKEN))
            {
                throw new Exception(EXPECTIDORPLUSOP + " in line " + this.errorLineNumber);
            }
            //puts a LOAD or STORE operation and its operand in the operationPackage
            this.bci.generate(LOAD, symbolTable.getAddress(token.getValue()));
            if(nextToken.getType().equals(IDTOKEN) || nextToken.getType().equals(EOFTOKEN))
            {
                this.bci.generate(STORE, symbolTable.getAddress(this.tokenToStore.getValue()));
            }
        }
        return isValid;
    }

    private boolean parseInt(Token token) throws Exception
    {
        boolean isValid = true;
        Token nextToken = nextToken();
        if(!parsingExpression)
        {
            throw new Exception(EXPECTID + " in line " + this.errorLineNumber);
        }
        if(parsingExpression)
        {
            if(nextToken.getType().equals(INTTOKEN) || nextToken.getType().equals(UNKNOWNTOKEN))
            {
                throw new Exception(EXPECTIDORPLUSOP + " in line " + this.errorLineNumber);
            }
            //puts a LOADI or STORE operation and its operand in the operationPackage
            this.bci.generate(LOADI, Integer.parseInt(token.getValue()));
            if(nextToken.getType().equals(IDTOKEN) || nextToken.getType().equals(EOFTOKEN))
            {
                this.bci.generate(STORE, symbolTable.getAddress(this.tokenToStore.getValue()));
            }
        }
        return isValid;
    }

    private boolean parsePlusOp() throws Exception
    {
        boolean isValid = true;
        Token previousToken = putToken();
        Token nextToken = nextToken();

        if(!parsingExpression)
        {
            throw new Exception(EXPECTID + " in line " + this.errorLineNumber);
        }
        if(parsingExpression)
        {
            if(!previousToken.getType().equals(INTTOKEN) && !previousToken.getType().equals(IDTOKEN) ||  !nextToken.getType().equals(INTTOKEN) && !nextToken.getType().equals(IDTOKEN))
            {
                throw new Exception(EXPECTIDORINT + " in line " + this.errorLineNumber);
            }
        }
        return isValid;
    }

    private boolean parseAssmt() throws Exception
    {
        boolean isValid = true;
        Token previousToken = putToken();
        Token nextToken = nextToken();
        if(!previousToken.getType().equals(IDTOKEN))
        {
            throw new Exception(EXPECTIDORINT + " in line " + this.errorLineNumber);
        }
        if(!nextToken.getType().equals(IDTOKEN) && !nextToken.getType().equals(INTTOKEN))
        {
            throw new Exception(EXPECTIDORINT + " in line " + this.errorLineNumber);
        }
        return isValid;
    }

    private void determineParsingExpression(Token token) throws Exception
    {
        Token nextToken = tokenList.get(index + 1);
        Token previousToken;
        if(index == 0)
        {
            previousToken = null;
        }
        else
        {
            previousToken = tokenList.get(index - 1);
        }

        if(token.getType().equals(ASSMTTOKEN))
        {
            parsingExpression = true;
        }
        if(token.getType().equals(IDTOKEN) && nextToken.getType().equals(ASSMTTOKEN))
        {
            parsingExpression = false;
        }
        if(token.getType().equals(INTTOKEN) && nextToken.getType().equals(ASSMTTOKEN))
        {
            throw new Exception(EXPECTIDORINT + " in line " + this.errorLineNumber);
        }
        if(previousToken != null && previousToken.getType().equals(PLUSTOKEN) && token.getType().equals(IDTOKEN) && nextToken.getType().equals(ASSMTTOKEN))
        {
            throw new Exception(EXPECTIDORINT + " in line " + this.errorLineNumber);
        }
    }

    private Token nextToken()
    {
        index = index + 1;
        return tokenList.get(index);
    }

    private Token putToken()
    {
        return tokenList.get(index - 1);
    }

    private void initialize(String fileName)
    {
        Lexer lexer = new Lexer(fileName);
        this.tokenList = lexer.getAllTokens();
    }

    private void setTokenToStore(Token token)
    {
        this.tokenToStore = token;
    }
}
