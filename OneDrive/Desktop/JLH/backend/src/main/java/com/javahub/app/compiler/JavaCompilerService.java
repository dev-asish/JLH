package com.javahub.app.compiler;

import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;

@Service
public class JavaCompilerService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final long MAX_EXECUTION_TIME = 5; // seconds
    private static final long MAX_MEMORY_MB = 128; // MB

    public CompilationResult compileAndRun(String code) {
        String className = extractClassName(code);
        if (className == null) {
            return new CompilationResult("", "Error: Could not find a public class in the code.");
        }

        Path tempDir = null;
        try {
            // Create temporary directory
            tempDir = Files.createTempDirectory(Paths.get(TEMP_DIR), "java_compile_");
            Path javaFile = tempDir.resolve(className + ".java");
            
            // Write code to file
            Files.write(javaFile, code.getBytes());

            // Compile
            String compileErrors = compileJavaFile(javaFile);
            if (!compileErrors.isEmpty()) {
                return new CompilationResult("", compileErrors);
            }

            // Run with sandbox restrictions
            String output = runJavaClass(tempDir, className);
            String errors = "";

            return new CompilationResult(output, errors);

        } catch (Exception e) {
            return new CompilationResult("", "Error: " + e.getMessage());
        } finally {
            // Cleanup
            if (tempDir != null) {
                deleteDirectory(tempDir.toFile());
            }
        }
    }

    private String extractClassName(String code) {
        // Simple extraction: look for "public class ClassName"
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ")) {
                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("class") && i + 1 < parts.length) {
                        String className = parts[i + 1];
                        // Remove { if present
                        className = className.replace("{", "").trim();
                        return className;
                    }
                }
            }
        }
        return null;
    }

    private String compileJavaFile(Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return "Error: Java compiler not available. Make sure JDK is installed, not just JRE.";
        }

        ByteArrayOutputStream compilerOut = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(compilerOut);

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            Iterable<? extends JavaFileObject> sourceFile = fileManager.getJavaFileObjects(javaFile.toFile());
            Iterable<String> options = Arrays.asList("-d", javaFile.getParent().toString());

            JavaCompiler.CompilationTask task =
                    compiler.getTask(writer, fileManager, diagnostics, options, null, sourceFile);

            boolean success = task.call();
            writer.flush();
            String compileOutput = compilerOut.toString();

            if (!success) {
                StringBuilder errorBuilder = new StringBuilder();
                for (javax.tools.Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errorBuilder.append(diagnostic.getMessage(null)).append("\n");
                }
                if (errorBuilder.length() > 0) {
                    return errorBuilder.toString();
                }
                return compileOutput;
            }
            return "";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        } finally {
            writer.close();
        }
    }

    private String runJavaClass(Path classDir, String className) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder output = new StringBuilder();
        StringBuilder errors = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-Xmx" + MAX_MEMORY_MB + "m",
                "-cp", classDir.toString(),
                className
            );

            // Set working directory
            processBuilder.directory(classDir.toFile());
            
            // Redirect error stream
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Read output in separate thread
            Future<String> outputFuture = executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                } catch (IOException e) {
                    return "Error reading output: " + e.getMessage();
                }
            });

            // Wait for process with timeout
            boolean finished = process.waitFor(MAX_EXECUTION_TIME, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                return "Error: Execution timeout (exceeded " + MAX_EXECUTION_TIME + " seconds)";
            }

            if (process.exitValue() != 0) {
                return "Error: Process exited with code " + process.exitValue();
            }

            String result = outputFuture.get(1, TimeUnit.SECONDS);
            return result;

        } catch (TimeoutException e) {
            return "Error: Execution timeout";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        } finally {
            executor.shutdownNow();
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    public static class CompilationResult {
        private String output;
        private String errors;

        public CompilationResult(String output, String errors) {
            this.output = output;
            this.errors = errors;
        }

        public String getOutput() {
            return output;
        }

        public String getErrors() {
            return errors;
        }
    }
}



