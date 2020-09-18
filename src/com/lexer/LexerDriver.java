package com.lexer;

import com.lexer.Lexer;

import java.util.ArrayList;

public class LexerDriver
{
    public static void main(String[] args)
    {
        String fileName="test.txt";
        if (args.length==0)
        {
            System.out.println("You must specify a file name");
        }
        else
        {
            fileName = args[0];
        }
        Lexer lexer = new Lexer(fileName);
        System.out.println(lexer.buffer);
        ArrayList<Token>tokenList = lexer.getAllTokens();
        for(int i = 0; i < tokenList.size(); i++)
        {
            Token token = tokenList.get(i);
            System.out.println("TOKEN TYPE: " + token.getType() + '\t' + "TOKEN VALUE: " + token.getValue() + '\t' + "TOKEN LINE NUMBER: " + token.getLineNumber());
        }
    }
}
