package com.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to build an array of Tokens from an input file
 * @author wolberd
 * @see //com.lexer.Token
 * @see //Parser
 * @see //ByteCodeInterpreter
 */
public class Lexer
{

    String buffer;
    private Integer currentLineNumber = 1;
    int index = 0;
    public static final String INTTOKEN="INT";
    public static final String IDTOKEN="ID";
    public static final String ASSMTTOKEN="ASSMT";
    public static final String PLUSTOKEN="PLUS";
    public static final String EOFTOKEN="EOF";
    public static final String UNKNOWNTOKEN="UNKNOWN";

    public Lexer(String fileName)
    {
        getInput(fileName);
    }

    //gets a singular token from the buffer
    public Token getNextToken()
    {
        Token token = null;

        //checking if lexer reached the end of the file
        if(index == buffer.length())
        {
            String tokenValue = "-";
            token = new Token(EOFTOKEN, tokenValue);
            token.setLineNumber(currentLineNumber);
            return token;
        }

        char tokenChar = buffer.charAt(index);
        int tokenAscii = tokenChar;
        if(tokenChar == '\n')
        {
            currentLineNumber++;
        }


        //checking if token is an integer
        if(48 <= tokenAscii && tokenAscii <= 57)
        {
            String tokenValue = getInteger();
            token = new Token(INTTOKEN, tokenValue);
            token.setLineNumber(currentLineNumber);
            return token;
        }
        //checking if token is an identifier
        else if((65 <= tokenAscii && tokenAscii <= 90) || (97 <= tokenAscii && tokenAscii <= 122))
        {
            String tokenValue = getIdentifier();
            token = new Token(IDTOKEN, tokenValue);
            token.setLineNumber(currentLineNumber);
            return token;
        }
        //checking if token is an assignment operator
        else if(tokenChar == '=')
        {
            String tokenValue = Character.toString(tokenChar);
            token = new Token(ASSMTTOKEN, tokenValue);
            index = index + 1;
            token.setLineNumber(currentLineNumber);
            return token;
        }
        //checking if token is a plus operator
        else if(tokenChar == '+')
        {
            String tokenValue = Character.toString(tokenChar);
            token = new Token(PLUSTOKEN, tokenValue);
            index = index + 1;
            token.setLineNumber(currentLineNumber);
            return token;
        }
        else if(!(1 <= tokenAscii && tokenAscii <= 32))
        {
            String tokenValue = Character.toString(tokenChar);
            token = new Token(UNKNOWNTOKEN, tokenValue);
            index = index + 1;
            token.setLineNumber(currentLineNumber);
            return token;
        }
        index = index + 1;
        return token;
    }

    public ArrayList<Token> getAllTokens()
    {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        while(index <= buffer.length())
        {
            if(index == buffer.length())
            {
                Token token = getNextToken();
                tokenList.add(token);
                break;
            }

            Token token = getNextToken();
            if(token != null)
            {
                tokenList.add(token);
            }
        }
        return tokenList;
    }


    //gets an integer token from buffer
    private String getInteger()
    {
        String tokenValue = Character.toString(buffer.charAt(index));
        boolean isTokenInt = true;

        while(isTokenInt)
        {
            if(index < buffer.length())
            {
                index = index + 1;
                if(index == buffer.length())
                {
                    return tokenValue;
                }
                char tokenChar = buffer.charAt(index);
                int tokenAscii = tokenChar;
                if (48 <= tokenAscii && tokenAscii <= 57)
                {
                    tokenValue = tokenValue + tokenChar;
                }
                else
                {
                    return tokenValue;
                }
            }
            else
            {
                isTokenInt = false;
            }
        }
        return tokenValue;
    }

    //gets an identifier token from buffer
    private String getIdentifier()
    {
        String tokenValue = Character.toString(buffer.charAt(index));
        boolean isTokenId = true;

        while(isTokenId)
        {
            if(index < buffer.length())
            {
                index = index + 1;
                if(index == buffer.length())
                {
                    return tokenValue;
                }
                char tokenChar = buffer.charAt(index);
                int tokenAscii = tokenChar;
                if ((65 <= tokenAscii && tokenAscii <= 90) || (97 <= tokenAscii && tokenAscii <= 122) || (48 <= tokenAscii && tokenAscii <= 57))
                {
                    tokenValue = tokenValue + tokenChar;
                }
                else
                {
                    return tokenValue;
                }
            }
            else
            {
                isTokenId = false;
            }
        }
        return tokenValue;
    }

    private void getInput(String fileName)
    {
        try
        {
            Path filePath = Paths.get(fileName);
            byte[] allBytes = Files.readAllBytes(filePath);
            buffer = new String (allBytes);
        }
        catch (IOException e)
        {
            System.out.println ("You did not enter a valid file name in the run arguments.");
            System.out.println ("Please enter a string to be parsed:");
            Scanner scanner = new Scanner(System.in);
            buffer=scanner.nextLine();
        }
    }
}
	