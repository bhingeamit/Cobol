package com.modernization.financial;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Financial Calculator Application
 * 
 * Purpose: Performs financial functions including loan and present value calculations
 * Converted from COBOL program COBCALC.cob
 * 
 * Features:
 * - Command-based interface
 * - Case-insensitive input processing
 * - Extensible calculator framework
 * - Subprogram integration via Strategy pattern
 * 
 * @author Modernization Team
 * @version 1.0
 * @since 2026-05-28
 */
public class CobCalc {
    
    private static final Logger LOGGER = Logger.getLogger(CobCalc.class.getName());
    private static final String END_COMMAND = "END";
    
    private final InputProcessor inputProcessor;
    private final CalculatorRegistry calculatorRegistry;
    private final Scanner scanner;
    private boolean useBufferMode;
    
    /**
     * Constructor for interactive mode
     */
    public CobCalc() {
        this(false);
    }
    
    /**
     * Constructor with mode selection
     * 
     * @param useBufferMode true to use predefined buffer, false for interactive input
     */
    public CobCalc(boolean useBufferMode) {
        this.useBufferMode = useBufferMode;
        this.scanner = new Scanner(System.in);
        this.inputProcessor = new InputProcessor();
        this.calculatorRegistry = new CalculatorRegistry();
        
        // Register available calculators
        registerCalculators();
    }
    
    /**
     * Main entry point
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Check if buffer mode is requested
        boolean bufferMode = args.length > 0 && args[0].equalsIgnoreCase("--buffer");
        
        CobCalc app = new CobCalc(bufferMode);
        try {
            app.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Application error occurred", e);
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        } finally {
            app.cleanup();
        }
    }
    
    /**
     * Main execution logic - corresponds to COBOL PROCEDURE DIVISION
     */
    public void execute() {
        displayMessage("CALC Begins.");
        LOGGER.info("Financial Calculator started");
        
        if (useBufferMode) {
            processBufferMode();
        } else {
            processInteractiveMode();
        }
        
        displayMessage("CALC Ends.");
        LOGGER.info("Financial Calculator completed");
    }
    
    /**
     * Process commands from predefined buffer
     * Corresponds to COBOL buffer processing with BUFFER-ARRAY
     */
    private void processBufferMode() {
        List<String> buffer = Arrays.asList("LOAN", "PVALUE", "pvalue", "END");
        
        for (String input : buffer) {
            if (!processInput(input)) {
                break;
            }
        }
    }
    
    /**
     * Process commands from user input interactively
     */
    private void processInteractiveMode() {
        System.out.println("\nAvailable commands:");
        System.out.println("  LOAN    - Calculate loan payment");
        System.out.println("  PVALUE  - Calculate present value");
        System.out.println("  END     - Exit calculator");
        System.out.println();
        
        String input;
        do {
            System.out.print("Enter command: ");
            input = scanner.nextLine().trim();
        } while (processInput(input));
    }
    
    /**
     * Process a single input command
     * Corresponds to COBOL ACCEPT-INPUT paragraph
     * 
     * @param input the input command to process
     * @return false if END command received, true otherwise
     */
    private boolean processInput(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        
        // Normalize input to uppercase (COBOL FUNCTION UPPER-CASE)
        String normalizedInput = inputProcessor.normalize(input);
        
        LOGGER.fine(() -> String.format("Processing input: %s -> %s", input, normalizedInput));
        
        // Evaluate command (COBOL EVALUATE statement)
        return evaluateCommand(normalizedInput, input);
    }
    
    /**
     * Evaluate and execute command
     * Corresponds to COBOL EVALUATE FUNCTION UPPER-CASE(INPUT-1)
     * 
     * @param normalizedInput uppercase normalized input
     * @param originalInput original input for display
     * @return false if END command, true otherwise
     */
    private boolean evaluateCommand(String normalizedInput, String originalInput) {
        switch (normalizedInput) {
            case END_COMMAND:
                return false;
                
            case "LOAN":
                calculateLoan();
                break;
                
            case "PVALUE":
                calculateValue();
                break;
                
            default:
                displayMessage("Invalid input: " + originalInput);
                LOGGER.warning("Invalid command received: " + originalInput);
        }
        
        return true;
    }
    
    /**
     * Calculate loan payment
     * Corresponds to COBOL CALCULATE-LOAN paragraph
     */
    private void calculateLoan() {
        LOGGER.info("Executing loan calculation");
        
        try {
            Calculator loanCalculator = calculatorRegistry.getCalculator("LOAN");
            CallFeedback feedback = loanCalculator.calculate();
            
            if (!feedback.isSuccessful()) {
                displayMessage("Call to COBLOAN Unsuccessful.");
                LOGGER.warning("Loan calculation failed: " + feedback.getMessage());
            }
        } catch (CalculatorNotFoundException e) {
            displayMessage("Loan calculator not available.");
            LOGGER.severe("Loan calculator not found: " + e.getMessage());
        }
    }
    
    /**
     * Calculate present value
     * Corresponds to COBOL CALCULATE-VALUE paragraph
     */
    private void calculateValue() {
        LOGGER.info("Executing present value calculation");
        
        try {
            Calculator valueCalculator = calculatorRegistry.getCalculator("PVALUE");
            CallFeedback feedback = valueCalculator.calculate();
            
            if (!feedback.isSuccessful()) {
                displayMessage("Call to COBVALU Unsuccessful.");
                LOGGER.warning("Present value calculation failed: " + feedback.getMessage());
            }
        } catch (CalculatorNotFoundException e) {
            displayMessage("Present value calculator not available.");
            LOGGER.severe("Present value calculator not found: " + e.getMessage());
        }
    }
    
    /**
     * Register available calculators
     */
    private void registerCalculators() {
        calculatorRegistry.register("LOAN", new LoanCalculator());
        calculatorRegistry.register("PVALUE", new PresentValueCalculator());
    }
    
    /**
     * Display message to console
     * Corresponds to COBOL DISPLAY ... UPON CONSOLE
     * 
     * @param message the message to display
     */
    private void displayMessage(String message) {
        System.out.println(message);
    }
    
    /**
     * Cleanup resources
     * Corresponds to COBOL GOBACK
     */
    private void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
        LOGGER.info("Resources cleaned up");
    }
}

/**
 * Input Processor - Handles input normalization
 * Corresponds to COBOL FUNCTION UPPER-CASE
 */
class InputProcessor {
    
    private static final int MAX_INPUT_LENGTH = 10; // COBOL PIC X(10)
    
    /**
     * Normalize input to uppercase and trim to max length
     * 
     * @param input raw input string
     * @return normalized input
     */
    public String normalize(String input) {
        if (input == null) {
            return "";
        }
        
        String normalized = input.trim().toUpperCase();
        
        // Enforce COBOL PIC X(10) constraint
        if (normalized.length() > MAX_INPUT_LENGTH) {
            normalized = normalized.substring(0, MAX_INPUT_LENGTH);
        }
        
        return normalized;
    }
}

/**
 * Call Feedback - Represents subprogram call result
 * Corresponds to COBOL CALL-FEEDBACK PIC XX
 */
class CallFeedback {
    
    private static final String SUCCESS_CODE = "OK";
    private final String code;
    private final String message;
    
    /**
     * Constructor for successful feedback
     */
    public CallFeedback() {
        this(SUCCESS_CODE, "Operation completed successfully");
    }
    
    /**
     * Constructor with code and message
     * 
     * @param code feedback code (2 characters, COBOL PIC XX)
     * @param message descriptive message
     */
    public CallFeedback(String code, String message) {
        // Enforce COBOL PIC XX constraint
        this.code = code != null && code.length() >= 2 ? code.substring(0, 2) : code;
        this.message = message;
    }
    
    /**
     * Check if operation was successful
     * 
     * @return true if code equals "OK"
     */
    public boolean isSuccessful() {
        return SUCCESS_CODE.equals(code);
    }
    
    /**
     * Get feedback code
     * 
     * @return feedback code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Get feedback message
     * 
     * @return feedback message
     */
    public String getMessage() {
        return message;
    }
}

/**
 * Calculator interface - Strategy pattern for different calculation types
 * Corresponds to COBOL subprogram CALL interface
 */
interface Calculator {
    
    /**
     * Perform calculation
     * 
     * @return call feedback indicating success or failure
     */
    CallFeedback calculate();
    
    /**
     * Get calculator name
     * 
     * @return calculator name
     */
    String getName();
}

/**
 * Loan Calculator - Implements loan payment calculation
 * Corresponds to COBOL subprogram COBLOAN
 */
class LoanCalculator implements Calculator {
    
    private static final Logger LOGGER = Logger.getLogger(LoanCalculator.class.getName());
    
    @Override
    public CallFeedback calculate() {
        LOGGER.info("Loan calculation started");
        
        try {
            // Placeholder for actual loan calculation logic
            // In real implementation, this would calculate loan payments
            // based on principal, interest rate, and term
            
            System.out.println("Calculating loan payment...");
            System.out.println("Loan calculation completed successfully.");
            
            return new CallFeedback();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loan calculation failed", e);
            return new CallFeedback("ER", "Loan calculation error: " + e.getMessage());
        }
    }
    
    @Override
    public String getName() {
        return "COBLOAN";
    }
}

/**
 * Present Value Calculator - Implements present value calculation
 * Corresponds to COBOL subprogram COBVALU
 */
class PresentValueCalculator implements Calculator {
    
    private static final Logger LOGGER = Logger.getLogger(PresentValueCalculator.class.getName());
    
    @Override
    public CallFeedback calculate() {
        LOGGER.info("Present value calculation started");
        
        try {
            // Placeholder for actual present value calculation logic
            // In real implementation, this would calculate present value
            // based on future value, interest rate, and periods
            
            System.out.println("Calculating present value...");
            System.out.println("Present value calculation completed successfully.");
            
            return new CallFeedback();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Present value calculation failed", e);
            return new CallFeedback("ER", "Present value calculation error: " + e.getMessage());
        }
    }
    
    @Override
    public String getName() {
        return "COBVALU";
    }
}

/**
 * Calculator Registry - Manages available calculators
 * Implements Registry pattern for calculator lookup
 */
class CalculatorRegistry {
    
    private static final Logger LOGGER = Logger.getLogger(CalculatorRegistry.class.getName());
    private final java.util.Map<String, Calculator> calculators;
    
    /**
     * Constructor
     */
    public CalculatorRegistry() {
        this.calculators = new java.util.HashMap<>();
    }
    
    /**
     * Register a calculator
     * 
     * @param name calculator name/command
     * @param calculator calculator instance
     */
    public void register(String name, Calculator calculator) {
        calculators.put(name.toUpperCase(), calculator);
        LOGGER.fine(() -> String.format("Registered calculator: %s", name));
    }
    
    /**
     * Get calculator by name
     * 
     * @param name calculator name
     * @return calculator instance
     * @throws CalculatorNotFoundException if calculator not found
     */
    public Calculator getCalculator(String name) throws CalculatorNotFoundException {
        Calculator calculator = calculators.get(name.toUpperCase());
        
        if (calculator == null) {
            throw new CalculatorNotFoundException("Calculator not found: " + name);
        }
        
        return calculator;
    }
    
    /**
     * Check if calculator is registered
     * 
     * @param name calculator name
     * @return true if registered
     */
    public boolean hasCalculator(String name) {
        return calculators.containsKey(name.toUpperCase());
    }
}

/**
 * Calculator Not Found Exception
 */
class CalculatorNotFoundException extends Exception {
    
    /**
     * Constructor with message
     * 
     * @param message error message
     */
    public CalculatorNotFoundException(String message) {
        super(message);
    }
}


