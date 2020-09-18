package com.parser;
import com.lexer.Token;
import java.util.ArrayList;

import java.util.HashMap;

public class SymbolTable
{
    public HashMap symbolTable;
    private Integer lastAddress = 0;

    public SymbolTable()
    {
        initializeTable();
    }

    @Override
    public String toString()
    {
        return "SymbolTable{" +
                "symbolTable=" + symbolTable +
                '}';
    }

    public boolean equals(SymbolTable symbolTable)
    {
        if(new ArrayList<>(symbolTable.getTable().values()).equals(new ArrayList<>(this.symbolTable.values())) && symbolTable.getTable().keySet().equals(this.symbolTable.keySet()))
        {
            return true;
        }
        return false;
    }

    public boolean isInTable(Token token)
    {
        String id = token.getValue();
        if(symbolTable.containsKey(id))
        {
            return true;
        }
        return false;
    }

    public Integer size()
    {
        return this.lastAddress;
    }

    public void addIdentifier(String id)
    {
        if(symbolTable.containsKey(id))
        {
            return;
        }
        symbolTable.put(id, lastAddress);
        generateAddress();

    }

    public HashMap<String, Integer> getTable()
    {
        return this.symbolTable;
    }
    public Integer getAddress(String id)
    {
        Integer address = (Integer) symbolTable.get(id);
        if(address == null)
        {
            return -1;
        }
        return address;
    }

    private void generateAddress()
    {
        lastAddress++;
    }

    private void initializeTable()
    {
        symbolTable = new HashMap<String, Integer>();
    }
}

