package com.interpreter;

import com.parser.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class ByteCodeInterpreter
{
    private int accumulator;
    private int memorySize;
    private SymbolTable symbolTable;
    //private ArrayList <HashMap<Integer, Integer>> operationPackage;
    private ArrayList <Integer> bytecode;
    private ArrayList <Integer> memory;

    private static final Integer LOAD = 0;
    private static final Integer LOADI = 1;
    private static final Integer STORE = 2;

    public ByteCodeInterpreter(Integer memorySize, SymbolTable symbolTable)
    {
        initialize(memorySize, symbolTable);
    }

    @Override
    public String toString()
    {
        return "ByteCodeInterpreter{" +
                "accumulator=" + accumulator +
                ", memorySize=" + memorySize +
                ", symbolTable=" + symbolTable.toString() +
                ", bytecode=" + bytecode +
                ", memory=" + memory +
                '}';
    }

    public boolean equals(ByteCodeInterpreter bci)
    {
        if(this.bytecode.size() != bci.getByteCode().size() || this.memory.size() != bci.getMemory().size())
        {
            return false;
        }
        for(int i = 0; i < this.bytecode.size(); i++)
        {
            if(!this.bytecode.get(i).equals(bci.getByteCode().get(i)))
            {
                return false;
            }
        }
        for(int x = 0; x < this.memory.size(); x++)
        {
            if(!this.memory.get(x).equals(bci.getMemory().get(x)))
            {
                return false;
            }
        }
        return true;
    }

    public int getAccumulator()
    {
        return this.accumulator;
    }
    public int getMemorySize()
    {
        return this.memorySize;
    }

    public SymbolTable getSymbolTable()
    {
        return this.symbolTable;
    }
    public ArrayList <Integer> getByteCode()
    {
        return this.bytecode;
    }
    public ArrayList <Integer> getMemory()
    {
        return this.memory;
    }

    public void run() throws Exception
    {
        for(int i = 1; i <= this.bytecode.size(); i=i+2)
        {
            Integer command = this.bytecode.get(i - 1);
            Integer operand = this.bytecode.get(i);
            if (command.equals(LOAD))
            {
                loadCommand(operand);
            }
            else if (command.equals(LOADI))
            {
                loadiCommand(operand);
            }
            else if (command.equals(STORE))
            {
                storeCommand(operand);
            }
            else
            {
                throw new Exception("Run-time Error: No such command exists");
            }
        }
    }

    public void generate(Integer command, Integer operand) throws Exception
    {
        if(command.equals(LOAD) || command.equals(LOADI) || command.equals(STORE))
        {
            this.bytecode.add(command);
            this.bytecode.add(operand);
        }
        else
        {
            throw new Exception("Run-time Error: No such command exists");
        }
    }

    private void loadCommand(Integer operand) throws Exception
    {
        System.out.println(this.symbolTable.toString());
        if(this.symbolTable.size() > this.memorySize)
        {
            throw new Exception("Error: Address out of range");
        }
        this.accumulator = this.accumulator + this.memory.get(operand);
    }

    private void loadiCommand(Integer operand) throws Exception
    {
        System.out.println(this.symbolTable.toString());
        if(this.symbolTable.size() > this.memorySize)
        {
            throw new Exception("Error: Address out of range");
        }
        this.accumulator = this.accumulator + operand;
    }

    private void storeCommand(Integer operand) throws Exception
    {
        System.out.println(this.symbolTable.toString());
        if(this.symbolTable.size() > this.memorySize)
        {
            throw new Exception("Error: Address out of range");
        }
        this.memory.set(operand, this.accumulator);
        this.accumulator = 0;
    }

    //sets accumulator, symbolTable, memory size, initializes bytecode list, and initializes memory
    private void initialize(Integer memorySize, SymbolTable symbolTable)
    {
        this.accumulator = 0;
        this.symbolTable = symbolTable;
        this.memorySize = memorySize;
        this.bytecode = new ArrayList <Integer> ();
        initializeMemory();
    }

    private void initializeMemory()
    {
        if(this.memory == null)
        {
            this.memory = new ArrayList <Integer>();
        }
        for(int i = 0; i < this.memorySize; i++)
        {
            this.memory.add(0);
        }
    }
}
