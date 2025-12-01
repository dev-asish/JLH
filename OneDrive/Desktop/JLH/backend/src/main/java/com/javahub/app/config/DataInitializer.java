package com.javahub.app.config;

import com.javahub.app.model.Course;
import com.javahub.app.model.CourseContent;
import com.javahub.app.model.Topic;
import com.javahub.app.model.UserDashboard;
import com.javahub.app.practice.model.PracticeQuestion;
import com.javahub.app.quiz.model.QuizQuestion;
import com.javahub.app.repository.CourseRepository;
import com.javahub.app.repository.CourseContentRepository;
import com.javahub.app.repository.TopicRepository;
import com.javahub.app.repository.UserDashboardRepository;
import com.javahub.app.repository.UserRepository;
import com.javahub.app.practice.repository.PracticeQuestionRepository;
import com.javahub.app.quiz.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PracticeQuestionRepository practiceQuestionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CourseContentRepository courseContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDashboardRepository userDashboardRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeCourses();
        // Initialize course content (check by title to avoid duplicates)
        initializeCourseContent();

        // Initialize topics with categories (check by title to avoid duplicates)
        initializeTopics();

        // Initialize practice questions
        initializePracticeQuestions();

        // Initialize quiz questions (idempotent - check by question text)
        initializeQuizQuestions();

        // Ensure each user has a dashboard record
        initializeUserDashboards();
    }

    private void initializeCourses() {
        createCourseIfNotExists("Java Basics",
                "Learn the fundamentals of Java programming including variables, data types, and control structures.");
        createCourseIfNotExists("Object-Oriented Programming",
                "Master OOP concepts: classes, objects, inheritance, polymorphism, and encapsulation.");
        createCourseIfNotExists("Spring Framework",
                "Build enterprise applications with Spring Boot, dependency injection, and REST APIs.");
    }

    private void createCourseIfNotExists(String title, String description) {
        courseRepository.findByTitle(title).orElseGet(() -> {
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            return courseRepository.save(course);
        });
    }

    private void initializePracticeQuestions() {
        createPracticeQuestionIfNotExists(
                "Sum of Two Numbers",
                "Write a Java program that takes two integers as input and returns their sum.",
                "Beginner",
                "5\n10",
                "15"
        );

        createPracticeQuestionIfNotExists(
                "Find Maximum in Array",
                "Write a method that finds and returns the maximum value in an array of integers.",
                "Beginner",
                "[3, 7, 2, 9, 1]",
                "9"
        );

        createPracticeQuestionIfNotExists(
                "Reverse a String",
                "Write a method that takes a string as input and returns the reversed string without using built-in reverse methods.",
                "Beginner",
                "\"Hello\"",
                "\"olleH\""
        );

        createPracticeQuestionIfNotExists(
                "Check Prime Number",
                "Write a method that checks if a given number is prime. Return true if prime, false otherwise.",
                "Intermediate",
                "17",
                "true"
        );

        createPracticeQuestionIfNotExists(
                "Fibonacci Sequence",
                "Write a method that returns the nth number in the Fibonacci sequence. The sequence starts with 0, 1, 1, 2, 3, 5...",
                "Intermediate",
                "7",
                "13"
        );

        createPracticeQuestionIfNotExists(
                "Palindrome Check",
                "Write a method that checks if a given string is a palindrome (reads the same forwards and backwards). Ignore case and spaces.",
                "Intermediate",
                "\"A man a plan a canal Panama\"",
                "true"
        );

        createPracticeQuestionIfNotExists(
                "Two Sum Problem",
                "Given an array of integers and a target sum, find two numbers that add up to the target. Return their indices.",
                "Intermediate",
                "Array: [2, 7, 11, 15], Target: 9",
                "[0, 1]"
        );

        createPracticeQuestionIfNotExists(
                "Binary Search",
                "Implement binary search algorithm to find a target value in a sorted array. Return the index if found, -1 otherwise.",
                "Intermediate",
                "Array: [1, 3, 5, 7, 9, 11], Target: 7",
                "3"
        );

        createPracticeQuestionIfNotExists(
                "Merge Two Sorted Arrays",
                "Write a method that merges two sorted arrays into one sorted array without using built-in sort methods.",
                "Advanced",
                "Array1: [1, 3, 5], Array2: [2, 4, 6]",
                "[1, 2, 3, 4, 5, 6]"
        );

        createPracticeQuestionIfNotExists(
                "Valid Parentheses",
                "Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. An input string is valid if: 1) Open brackets must be closed by the same type of brackets. 2) Open brackets must be closed in the correct order.",
                "Advanced",
                "\"([{}])\"",
                "true"
        );
    }

    private void createPracticeQuestionIfNotExists(String title, String description, String difficulty, String sampleInput, String sampleOutput) {
        practiceQuestionRepository.findByTitle(title)
                .orElseGet(() -> practiceQuestionRepository.save(
                        new PracticeQuestion(title, description, difficulty, sampleInput, sampleOutput)
                ));
    }

    private void initializeQuizQuestions() {
        // Helper method to create quiz question if it doesn't exist
        java.util.function.Consumer<QuizQuestion> createIfNotExists = question -> {
            if (quizQuestionRepository.findByQuestion(question.getQuestion()).isEmpty()) {
                quizQuestionRepository.save(question);
            }
        };

        // Existing questions (keeping for backward compatibility)
        if (quizQuestionRepository.count() == 0) {
            // Basics Topic - Beginner
            quizQuestionRepository.save(new QuizQuestion(
                "What is the default value of an int variable in Java?",
                "0",
                "null",
                "undefined",
                "-1",
                "A",
                "In Java, primitive types have default values. For int, the default value is 0.",
                "Basics"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Which keyword is used to declare a constant in Java?",
                "const",
                "final",
                "static",
                "constant",
                "B",
                "The 'final' keyword is used to declare constants in Java. Once assigned, a final variable cannot be changed.",
                "Basics"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "What is the size of a boolean variable in Java?",
                "1 bit",
                "1 byte",
                "4 bytes",
                "Size is not precisely defined",
                "D",
                "The size of a boolean is not precisely defined in the Java specification. It depends on the JVM implementation.",
                "Basics"
            ));

            // OOP Topic - Beginner/Intermediate
            quizQuestionRepository.save(new QuizQuestion(
                "What is the main purpose of encapsulation in OOP?",
                "To hide implementation details",
                "To improve performance",
                "To reduce code size",
                "To enable multiple inheritance",
                "A",
                "Encapsulation is about bundling data and methods together and hiding internal implementation details from outside access.",
                "OOP"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Which keyword is used to achieve inheritance in Java?",
                "implements",
                "extends",
                "inherits",
                "super",
                "B",
                "The 'extends' keyword is used to create a subclass that inherits from a superclass in Java.",
                "OOP"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "What is method overriding?",
                "Creating a new method with the same name",
                "Providing a specific implementation in a subclass",
                "Hiding a method from parent class",
                "Changing method parameters",
                "B",
                "Method overriding occurs when a subclass provides its own implementation of a method that is already defined in its parent class.",
                "OOP"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Can a class extend multiple classes in Java?",
                "Yes, unlimited",
                "Yes, up to 2",
                "No, only one",
                "Yes, but only interfaces",
                "C",
                "Java supports single inheritance for classes. A class can extend only one parent class, but can implement multiple interfaces.",
                "OOP"
            ));

            // Collections Topic - Intermediate
            quizQuestionRepository.save(new QuizQuestion(
                "Which collection class is synchronized by default?",
                "ArrayList",
                "HashMap",
                "Vector",
                "HashSet",
                "C",
                "Vector is synchronized by default, making it thread-safe but slower than ArrayList.",
                "Collections"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "What is the difference between ArrayList and LinkedList?",
                "No difference",
                "ArrayList uses array, LinkedList uses nodes",
                "LinkedList is faster for all operations",
                "ArrayList cannot store objects",
                "B",
                "ArrayList uses a dynamic array internally, while LinkedList uses a doubly-linked list of nodes.",
                "Collections"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Which interface does HashMap implement?",
                "List",
                "Set",
                "Map",
                "Collection",
                "C",
                "HashMap implements the Map interface, which stores key-value pairs.",
                "Collections"
            ));

            // Exception Handling - Intermediate
            quizQuestionRepository.save(new QuizQuestion(
                "What is the parent class of all exceptions in Java?",
                "Error",
                "RuntimeException",
                "Throwable",
                "Exception",
                "C",
                "Throwable is the superclass of all errors and exceptions in Java.",
                "Exceptions"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Which exception is checked?",
                "NullPointerException",
                "ArrayIndexOutOfBoundsException",
                "IOException",
                "ArithmeticException",
                "C",
                "IOException is a checked exception, meaning it must be handled or declared in the method signature.",
                "Exceptions"
            ));

            // Advanced Topics
            quizQuestionRepository.save(new QuizQuestion(
                "What is the purpose of the 'static' keyword?",
                "To make a variable constant",
                "To create class-level members",
                "To prevent inheritance",
                "To enable polymorphism",
                "B",
                "The 'static' keyword is used to create class-level members that belong to the class rather than instances.",
                "Advanced"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "What is a lambda expression used for?",
                "Exception handling",
                "Functional programming",
                "Memory management",
                "File I/O",
                "B",
                "Lambda expressions enable functional programming in Java by allowing you to treat functions as method arguments.",
                "Advanced"
            ));

            quizQuestionRepository.save(new QuizQuestion(
                "Which annotation is used for dependency injection in Spring?",
                "@Inject",
                "@Autowired",
                "@Component",
                "@Service",
                "B",
                "@Autowired is Spring's annotation for dependency injection, automatically wiring beans together.",
                "Advanced"
            ));
        }

        // Additional 30+ quiz questions across categories
        // Basics Category
        createIfNotExists.accept(new QuizQuestion(
            "What is the range of a byte in Java?",
            "-128 to 127",
            "0 to 255",
            "-256 to 255",
            "-32768 to 32767",
            "A",
            "A byte in Java is 8 bits, ranging from -128 to 127.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which operator is used for string concatenation in Java?",
            "+",
            "&",
            "concat()",
            "Both A and C",
            "D",
            "Java uses the + operator for string concatenation, which internally uses StringBuilder. The concat() method also works.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the output of: System.out.println(5 / 2);",
            "2.5",
            "2",
            "2.0",
            "Error",
            "B",
            "Integer division in Java truncates the decimal part, so 5/2 = 2.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which of these is a valid identifier in Java?",
            "2variable",
            "_variable",
            "variable-name",
            "class",
            "B",
            "Java identifiers can start with underscore or letter, but not numbers. Keywords like 'class' cannot be used.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the default value of a String variable?",
            "empty string",
            "null",
            "\"\"",
            "undefined",
            "B",
            "String is a reference type, so its default value is null.",
            "Basics"
        ));

        // Arrays Category
        createIfNotExists.accept(new QuizQuestion(
            "How do you get the length of an array in Java?",
            "array.length()",
            "array.length",
            "array.size()",
            "array.count()",
            "B",
            "Arrays use the length property (not a method) to get the number of elements.",
            "Arrays"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What happens when you access an array index that doesn't exist?",
            "Returns null",
            "Returns 0",
            "Throws ArrayIndexOutOfBoundsException",
            "Returns -1",
            "C",
            "Accessing an invalid array index throws ArrayIndexOutOfBoundsException.",
            "Arrays"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "How do you initialize a 2D array in Java?",
            "int[][] arr = new int[3][4];",
            "int arr[][] = new int[3,4];",
            "int arr = int[3][4];",
            "int[][] arr = int[3][4];",
            "A",
            "2D arrays are declared with [][] and initialized with new keyword.",
            "Arrays"
        ));

        // Strings Category
        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between == and equals() for Strings?",
            "No difference",
            "== compares references, equals() compares values",
            "== compares values, equals() compares references",
            "Both compare values",
            "B",
            "== checks if two references point to the same object, while equals() compares the actual string content.",
            "Strings"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which method is used to get a substring in Java?",
            "substr()",
            "substring()",
            "slice()",
            "cut()",
            "B",
            "The substring() method extracts a portion of a string in Java.",
            "Strings"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "When should you use StringBuilder instead of String concatenation?",
            "Always",
            "Never",
            "When concatenating in loops",
            "Only for single concatenation",
            "C",
            "StringBuilder is more efficient when performing multiple concatenations, especially in loops.",
            "Strings"
        ));

        // OOP Category
        createIfNotExists.accept(new QuizQuestion(
            "What is an abstract class?",
            "A class that cannot be instantiated",
            "A class with only abstract methods",
            "A class that must be extended",
            "All of the above",
            "A",
            "An abstract class cannot be instantiated directly and may contain abstract methods.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between an interface and an abstract class?",
            "No difference",
            "Interface supports multiple inheritance, abstract class doesn't",
            "Abstract class can have constructors, interface cannot",
            "Both B and C",
            "D",
            "Interfaces support multiple inheritance and cannot have constructors, while abstract classes can have constructors but support single inheritance.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is method overloading?",
            "Same method name, different parameters",
            "Same method name, same parameters, different return type",
            "Different method name, same parameters",
            "Same method in different classes",
            "A",
            "Method overloading allows multiple methods with the same name but different parameter lists.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the 'super' keyword used for?",
            "To call parent class constructor",
            "To access parent class members",
            "To call parent class methods",
            "All of the above",
            "D",
            "The 'super' keyword is used to access parent class members, methods, and constructors.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is a constructor?",
            "A method that returns an object",
            "A special method to initialize objects",
            "A static method",
            "A private method",
            "B",
            "A constructor is a special method called when an object is created to initialize it.",
            "OOP"
        ));

        // Collections Category
        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between HashSet and TreeSet?",
            "No difference",
            "HashSet is unordered, TreeSet is sorted",
            "TreeSet is faster",
            "HashSet allows duplicates",
            "B",
            "HashSet stores elements in no particular order, while TreeSet maintains sorted order.",
            "Collections"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which collection does not allow duplicate elements?",
            "ArrayList",
            "LinkedList",
            "HashSet",
            "HashMap",
            "C",
            "HashSet implements the Set interface, which does not allow duplicate elements.",
            "Collections"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between HashMap and Hashtable?",
            "No difference",
            "HashMap is synchronized, Hashtable is not",
            "Hashtable is synchronized, HashMap is not",
            "HashMap allows null keys",
            "C",
            "Hashtable is synchronized (thread-safe) while HashMap is not. HashMap allows one null key.",
            "Collections"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What does the Iterator interface provide?",
            "A way to traverse collections",
            "A way to modify collections",
            "A way to sort collections",
            "A way to filter collections",
            "A",
            "Iterator provides a way to traverse through collection elements sequentially.",
            "Collections"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which method is used to add elements to an ArrayList?",
            "push()",
            "add()",
            "insert()",
            "append()",
            "B",
            "The add() method is used to add elements to an ArrayList.",
            "Collections"
        ));

        // Exception Handling Category
        createIfNotExists.accept(new QuizQuestion(
            "What is a checked exception?",
            "An exception that must be caught or declared",
            "An exception that occurs at runtime",
            "An exception that cannot be caught",
            "An exception in checked code",
            "A",
            "Checked exceptions must be handled with try-catch or declared in the method signature.",
            "Exception Handling"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the finally block used for?",
            "To handle exceptions",
            "To execute code regardless of exceptions",
            "To catch exceptions",
            "To throw exceptions",
            "B",
            "The finally block executes code regardless of whether an exception occurs or not.",
            "Exception Handling"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between throw and throws?",
            "No difference",
            "throw is used in method, throws is in signature",
            "throws is used in method, throw is in signature",
            "Both are keywords",
            "B",
            "'throw' is used to throw an exception inside a method, while 'throws' declares exceptions in the method signature.",
            "Exception Handling"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "Which exception is thrown when dividing by zero?",
            "NullPointerException",
            "ArithmeticException",
            "NumberFormatException",
            "IllegalArgumentException",
            "B",
            "Dividing by zero throws ArithmeticException in Java.",
            "Exception Handling"
        ));

        // Multithreading Category
        createIfNotExists.accept(new QuizQuestion(
            "What is a thread in Java?",
            "A process",
            "A lightweight sub-process",
            "A method",
            "A variable",
            "B",
            "A thread is a lightweight sub-process that allows concurrent execution.",
            "Multithreading"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "How do you create a thread in Java?",
            "Extend Thread class",
            "Implement Runnable interface",
            "Both A and B",
            "Use Thread.create()",
            "C",
            "Threads can be created by extending Thread class or implementing Runnable interface.",
            "Multithreading"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the synchronized keyword used for?",
            "To make code thread-safe",
            "To speed up execution",
            "To prevent compilation",
            "To enable multithreading",
            "A",
            "The synchronized keyword ensures only one thread can access a method or block at a time.",
            "Multithreading"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the purpose of the wait() method?",
            "To pause thread execution",
            "To stop a thread",
            "To make thread wait for notification",
            "To kill a thread",
            "C",
            "The wait() method makes a thread wait until it receives a notification from another thread.",
            "Multithreading"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is a deadlock?",
            "A thread that stops",
            "When threads wait for each other indefinitely",
            "A compilation error",
            "A runtime exception",
            "B",
            "A deadlock occurs when two or more threads are blocked forever, waiting for each other.",
            "Multithreading"
        ));

        // More Basics
        createIfNotExists.accept(new QuizQuestion(
            "What is the ternary operator in Java?",
            "?:",
            "??",
            "::",
            "++",
            "A",
            "The ternary operator ?: is a shorthand for if-else: condition ? value1 : value2.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the difference between ++i and i++?",
            "No difference",
            "++i is pre-increment, i++ is post-increment",
            "i++ is pre-increment, ++i is post-increment",
            "Both are invalid",
            "B",
            "++i increments before using the value, i++ increments after using the value.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is a package in Java?",
            "A folder",
            "A way to organize classes",
            "A collection of related classes",
            "Both B and C",
            "D",
            "A package is a namespace that organizes related classes and interfaces.",
            "Basics"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is the purpose of the import statement?",
            "To include libraries",
            "To use classes from other packages",
            "To export classes",
            "To compile code",
            "B",
            "The import statement allows you to use classes from other packages without fully qualifying their names.",
            "Basics"
        ));

        // More OOP
        createIfNotExists.accept(new QuizQuestion(
            "What is the 'this' keyword used for?",
            "To refer to current object",
            "To refer to parent class",
            "To create objects",
            "To delete objects",
            "A",
            "The 'this' keyword refers to the current instance of the class.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is method chaining?",
            "Calling multiple methods in sequence",
            "Linking methods together",
            "A design pattern",
            "All of the above",
            "D",
            "Method chaining allows calling multiple methods on the same object in a single statement.",
            "OOP"
        ));

        createIfNotExists.accept(new QuizQuestion(
            "What is a singleton pattern?",
            "A class with one method",
            "A class with only one instance",
            "A class with one field",
            "A design pattern for one object",
            "B",
            "Singleton pattern ensures a class has only one instance and provides global access to it.",
            "OOP"
        ));
    }

    private void initializeTopics() {
        // Helper method to create topic if it doesn't exist
        java.util.function.Consumer<Topic> createIfNotExists = topic -> {
            if (topicRepository.findByTitle(topic.getTitle()).isEmpty()) {
                topicRepository.save(topic);
            }
        };

        // Basics Category
        createIfNotExists.accept(new Topic("Variables", 
            "Variables in Java are containers that hold data values. Java has two types: primitive (int, double, boolean, char) and reference (String, arrays, objects). Variables must be declared with a specific type before use. Example: int age = 25;", 
            "Beginner", "Basics"));
        
        createIfNotExists.accept(new Topic("Operators", 
            "Java operators perform operations on variables and values. Types include: arithmetic (+, -, *, /, %), relational (==, !=, <, >), logical (&&, ||, !), assignment (=, +=, -=), and bitwise operators. Understanding operator precedence is crucial.", 
            "Beginner", "Basics"));
        
        createIfNotExists.accept(new Topic("Conditionals", 
            "Conditional statements control program flow based on conditions. Java provides if, if-else, if-else-if, and switch statements. The ternary operator (condition ? value1 : value2) is a shorthand for simple conditionals.", 
            "Beginner", "Basics"));
        
        createIfNotExists.accept(new Topic("Loops", 
            "Loops execute code repeatedly. Java has for loops (for initialization; condition; increment), while loops (while condition), do-while loops (do { } while condition), and enhanced for loops (for-each). Use break and continue to control loop execution.", 
            "Beginner", "Basics"));

        // Arrays Category
        createIfNotExists.accept(new Topic("1D Arrays", 
            "One-dimensional arrays store elements of the same type in a linear sequence. Declare with: int[] arr = new int[5]; or int[] arr = {1, 2, 3}; Access elements using index: arr[0]. Arrays have fixed size and are zero-indexed.", 
            "Beginner", "Arrays"));
        
        createIfNotExists.accept(new Topic("2D Arrays", 
            "Two-dimensional arrays are arrays of arrays, useful for matrices and tables. Declare with: int[][] matrix = new int[3][4]; Access with: matrix[row][col]. Can have irregular dimensions (jagged arrays).", 
            "Intermediate", "Arrays"));
        
        createIfNotExists.accept(new Topic("Matrix Basics", 
            "Matrices are 2D arrays used for mathematical operations. Common operations include: addition, multiplication, transposition, and finding determinants. Matrix operations are fundamental in algorithms and data processing.", 
            "Intermediate", "Arrays"));

        // Strings Category
        createIfNotExists.accept(new Topic("String Operations", 
            "Strings in Java are immutable objects. Common operations include: length(), charAt(), substring(), indexOf(), replace(), toUpperCase(), toLowerCase(), trim(), split(), and equals(). String concatenation uses + operator or concat() method.", 
            "Beginner", "Strings"));
        
        createIfNotExists.accept(new Topic("StringBuilder", 
            "StringBuilder is a mutable sequence of characters, more efficient than String for frequent modifications. Methods include: append(), insert(), delete(), reverse(), toString(). Use StringBuilder when building strings in loops.", 
            "Intermediate", "Strings"));
        
        createIfNotExists.accept(new Topic("StringBuffer", 
            "StringBuffer is similar to StringBuilder but thread-safe (synchronized). Use StringBuffer in multi-threaded environments. Methods are the same as StringBuilder. StringBuilder is preferred for single-threaded applications due to better performance.", 
            "Intermediate", "Strings"));

        // Functions Category
        createIfNotExists.accept(new Topic("Methods", 
            "Methods are blocks of code that perform specific tasks. Syntax: accessModifier returnType methodName(parameters) { body }. Methods can be static (class-level) or instance (object-level). Method overloading allows multiple methods with same name but different parameters.", 
            "Beginner", "Functions"));
        
        createIfNotExists.accept(new Topic("Recursion", 
            "Recursion is when a method calls itself. Requires a base case to prevent infinite recursion. Useful for problems like factorial, Fibonacci, tree traversal, and divide-and-conquer algorithms. Can be memory-intensive but elegant for certain problems.", 
            "Intermediate", "Functions"));

        // Searching/Sorting Category
        createIfNotExists.accept(new Topic("Linear Search", 
            "Linear search checks each element sequentially until target is found or end is reached. Time complexity: O(n). Simple to implement but inefficient for large datasets. Best for unsorted arrays or small datasets.", 
            "Beginner", "Searching/Sorting"));
        
        createIfNotExists.accept(new Topic("Binary Search", 
            "Binary search works on sorted arrays by repeatedly dividing search space in half. Time complexity: O(log n). Much faster than linear search but requires sorted data. Uses divide-and-conquer approach.", 
            "Intermediate", "Searching/Sorting"));
        
        createIfNotExists.accept(new Topic("Bubble Sort", 
            "Bubble sort repeatedly steps through the list, compares adjacent elements and swaps them if in wrong order. Time complexity: O(n²). Simple but inefficient. Best for educational purposes or very small datasets.", 
            "Beginner", "Searching/Sorting"));
        
        createIfNotExists.accept(new Topic("Selection Sort", 
            "Selection sort finds the minimum element and places it at the beginning, repeating for remaining elements. Time complexity: O(n²). More efficient than bubble sort in practice but still O(n²). Useful for understanding sorting concepts.", 
            "Beginner", "Searching/Sorting"));

        // OOP Category
        createIfNotExists.accept(new Topic("Classes", 
            "A class is a blueprint for creating objects. It defines properties (fields) and behaviors (methods). Classes encapsulate data and methods together. Example: public class Person { private String name; public void setName(String n) { name = n; } }", 
            "Beginner", "OOP"));
        
        createIfNotExists.accept(new Topic("Objects", 
            "An object is an instance of a class. Created using the 'new' keyword: Person p = new Person(); Objects have state (field values) and behavior (methods). Each object is independent with its own memory space.", 
            "Beginner", "OOP"));
        
        createIfNotExists.accept(new Topic("Inheritance", 
            "Inheritance allows a class to inherit properties and methods from another class. The subclass extends the superclass using 'extends' keyword. Promotes code reuse and establishes 'is-a' relationships. Java supports single inheritance for classes.", 
            "Intermediate", "OOP"));
        
        createIfNotExists.accept(new Topic("Polymorphism", 
            "Polymorphism means 'many forms'. Allows objects of different classes to be treated as objects of a common superclass. Achieved through method overriding and interfaces. Enables code flexibility and extensibility.", 
            "Intermediate", "OOP"));
        
        createIfNotExists.accept(new Topic("Abstraction", 
            "Abstraction hides implementation details and shows only essential features. Achieved through abstract classes (abstract keyword) and interfaces. Abstract methods have no body and must be implemented by subclasses.", 
            "Intermediate", "OOP"));
        
        createIfNotExists.accept(new Topic("Encapsulation", 
            "Encapsulation bundles data and methods together and restricts direct access. Achieved using access modifiers (private, protected, public). Provides data hiding and controlled access through getters and setters.", 
            "Beginner", "OOP"));

        // Advanced Category
        createIfNotExists.accept(new Topic("Collections", 
            "Java Collections Framework provides data structures like ArrayList, LinkedList, HashSet, HashMap, TreeSet, TreeMap. Collections are more flexible than arrays. Implement List, Set, Map interfaces. Use generics for type safety.", 
            "Intermediate", "Advanced"));
        
        createIfNotExists.accept(new Topic("Multithreading", 
            "Multithreading allows concurrent execution of multiple threads. Create threads by extending Thread class or implementing Runnable interface. Use synchronized keyword for thread safety. ExecutorService manages thread pools efficiently.", 
            "Advanced", "Advanced"));
        
        createIfNotExists.accept(new Topic("File Handling", 
            "File I/O in Java uses classes like File, FileReader, FileWriter, BufferedReader, BufferedWriter, Scanner. Use try-with-resources for automatic resource management. Handle IOException for file operations. NIO.2 provides modern file APIs.", 
            "Intermediate", "Advanced"));
        
        createIfNotExists.accept(new Topic("Exception Handling", 
            "Exceptions are runtime errors. Handle with try-catch-finally blocks. Use throws to declare exceptions. Checked exceptions must be handled; unchecked exceptions (RuntimeException) are optional. Custom exceptions extend Exception class.", 
            "Intermediate", "Advanced"));
    }

    private void initializeCourseContent() {
        // Helper method to create course content if it doesn't exist
        java.util.function.Consumer<CourseContent> createIfNotExists = content -> {
            List<CourseContent> existing = courseContentRepository.findByCourseIdOrderByOrderNumberAsc(content.getCourseId());
            boolean exists = existing.stream().anyMatch(c -> 
                c.getTitle().equals(content.getTitle()) && c.getOrderNumber() == content.getOrderNumber());
            if (!exists) {
                courseContentRepository.save(content);
            }
        };

        // Course 1: Java Basics
        createIfNotExists.accept(new CourseContent(1, "Introduction to Java", 
            "Java is a high-level, object-oriented programming language developed by Sun Microsystems (now Oracle). It's platform-independent, meaning Java code can run on any device with a Java Virtual Machine (JVM). Java is known for its 'write once, run anywhere' principle.\n\nKey features:\n- Object-oriented\n- Platform independent\n- Secure\n- Robust\n- Multithreaded\n- High performance", 
            1));
        
        createIfNotExists.accept(new CourseContent(1, "Variables and Data Types", 
            "Variables are containers that store data values. Java has two categories of data types:\n\nPrimitive Types:\n- byte, short, int, long (integers)\n- float, double (floating-point)\n- char (single character)\n- boolean (true/false)\n\nReference Types:\n- String\n- Arrays\n- Objects\n\nExample:\nint age = 25;\nString name = \"Java\";\ndouble price = 19.99;", 
            2));
        
        createIfNotExists.accept(new CourseContent(1, "Control Structures", 
            "Control structures determine the flow of program execution:\n\nConditional Statements:\n- if-else: Executes code based on condition\n- switch: Multi-way branching\n\nLoops:\n- for: Iterates a fixed number of times\n- while: Continues while condition is true\n- do-while: Executes at least once\n\nExample:\nfor (int i = 0; i < 10; i++) {\n    System.out.println(i);\n}", 
            3));

        // Course 2: Object-Oriented Programming
        createIfNotExists.accept(new CourseContent(2, "Classes and Objects", 
            "A class is a blueprint for creating objects. It defines properties (fields) and behaviors (methods). An object is an instance of a class.\n\nExample:\npublic class Person {\n    private String name;\n    private int age;\n    \n    public Person(String name, int age) {\n        this.name = name;\n        this.age = age;\n    }\n    \n    public void display() {\n        System.out.println(name + \" is \" + age);\n    }\n}", 
            1));
        
        createIfNotExists.accept(new CourseContent(2, "Inheritance", 
            "Inheritance allows a class to inherit properties and methods from another class. The subclass extends the superclass using the 'extends' keyword.\n\nBenefits:\n- Code reusability\n- Method overriding\n- Polymorphism\n\nExample:\npublic class Animal {\n    public void eat() { ... }\n}\n\npublic class Dog extends Animal {\n    public void bark() { ... }\n}", 
            2));
        
        createIfNotExists.accept(new CourseContent(2, "Polymorphism and Encapsulation", 
            "Polymorphism allows objects of different classes to be treated as objects of a common superclass. Encapsulation bundles data and methods together, hiding implementation details.\n\nPolymorphism Example:\nAnimal animal = new Dog();\nanimal.eat(); // Calls Dog's eat() if overridden\n\nEncapsulation:\n- Use private fields\n- Provide public getters/setters\n- Control access to data", 
            3));

        // Course 3: Spring Framework
        createIfNotExists.accept(new CourseContent(3, "Introduction to Spring", 
            "Spring Framework is a comprehensive framework for building enterprise Java applications. It provides:\n\n- Dependency Injection (DI)\n- Aspect-Oriented Programming (AOP)\n- Spring MVC for web applications\n- Spring Boot for rapid development\n\nKey Benefits:\n- Loose coupling\n- Easy testing\n- Reduced boilerplate code\n- Modular architecture", 
            1));
        
        createIfNotExists.accept(new CourseContent(3, "Dependency Injection", 
            "Dependency Injection (DI) is a design pattern where objects receive their dependencies from external sources rather than creating them internally.\n\nTypes of DI:\n- Constructor Injection\n- Setter Injection\n- Field Injection\n\nExample with @Autowired:\n@Service\npublic class UserService {\n    @Autowired\n    private UserRepository repository;\n}", 
            2));
        
        createIfNotExists.accept(new CourseContent(3, "REST APIs with Spring Boot", 
            "Spring Boot simplifies REST API development:\n\n@RestController\npublic class UserController {\n    @GetMapping(\"/users\")\n    public List<User> getUsers() {\n        return userService.findAll();\n    }\n    \n    @PostMapping(\"/users\")\n    public User createUser(@RequestBody User user) {\n        return userService.save(user);\n    }\n}\n\nAnnotations:\n- @RestController\n- @RequestMapping\n- @GetMapping, @PostMapping, etc.\n- @RequestBody, @PathVariable", 
            3));
    }

    private void initializeUserDashboards() {
        userRepository.findAll().forEach(user ->
                userDashboardRepository.findByUserId(user.getId())
                        .orElseGet(() -> {
                            UserDashboard dashboard = new UserDashboard(user.getId());
                            return userDashboardRepository.save(dashboard);
                        })
        );
    }
}

