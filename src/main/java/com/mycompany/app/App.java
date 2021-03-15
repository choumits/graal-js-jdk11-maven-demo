/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mycompany.app;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import java.io.IOException;
import org.graalvm.polyglot.Source;

/**
 * Simple benchmark for Graal.js via GraalVM Polyglot Context and ScriptEngine.
 */
public class App {

    public static final int WARMUP = 15;
    public static final int ITERATIONS = 10;

    /*
     * Compile code using the TypeScript compiler while loading the ECMA2020 language definition to be able to do
     * type checking for native types.
     */
    public static final String COMPILE_TS = "function compileTypescript() {\n" +
            "\n" +
            "  const inputSourceCode = \"function add(val1 : string, val2 : string) { return val1 + val2}\";\n" +
            "\n" +
            "  const options = ts.getDefaultCompilerOptions();\n" +
            "  options.target = ts.ScriptTarget.ES2020;\n" +
            "\n" +
            "  let outputCode = undefined;\n" +
            "\n" +
            "  const fileName = 'file.ts';\n" +
            "\n" +
            "  const host = createCompilerHost(fileName, inputSourceCode, (data) => outputCode = data);\n" +
            "\n" +
            "  const program = ts.createProgram([fileName], options, host);\n" +
            "\n" +
            "  program.emit();\n" +
            "\n" +
            "  return outputCode;\n" +
            "}\n" +
            "function createCompilerHost(fileName, inputSourceCode, writeFile) {\n" +
            "  const sourceFile = ts.createSourceFile(fileName, inputSourceCode, ts.ScriptTarget.ES2020);\n" +
            "\n" +
            "  return {\n" +
            "    fileExists: (filePath) => filePath === fileName,\n" +
            "    directoryExists: (dirPath) => dirPath === '/',\n" +
            "    getCurrentDirectory: () => '/',\n" +
            "    getDirectories: () => [],\n" +
            "    getCanonicalFileName: (fileName) => fileName,\n" +
            "    getNewLine: () => '\\n',\n" +
            "    getDefaultLibFileName: function() { return 'lib.es2020.d.ts'; },\n" +
            "    getDefaultLibLocation: () => '',\n" +
            "\n" +
            "    getSourceFile: (filePath) => {\n" +
            "      if (filePath === fileName) {\n" +
            "        return sourceFile;\n" +
            "      } else {\n" +
            "        return ts.createSourceFile(filePath, getLibFileContent(filePath), ts.ScriptTarget.ES2020);\n" +
            "      }\n" +
            "    },\n" +
            "    readFile: (filePath) => filePath === fileName ? inputSourceCode : undefined,\n" +
            "    useCaseSensitiveFileNames: () => true,\n" +
            "    writeFile: (fileName, data) => writeFile(data),\n" +
            "  };\n" +
            "}\n";

    /*
     * Compile code using the TypeScript compiler without doing any type checking or loading the libraries necessary
     * to do so.
     */
    public static final String COMPILE_TS_NO_TYPE_CHECK = "function compileTypescript() {\n" +
            "\n" +
            "  let inputSourceCode = \"function add(val1, val2) { return val1 + val2}\";\n" +
            "\n" +
            "  const options = ts.getDefaultCompilerOptions();\n" +
            "  options.target = ts.ScriptTarget.ES2020;\n" +
            "\n" +
            "  let outputCode = undefined;\n" +
            "\n" +
            "  const fileName = 'file.ts';\n" +
            "\n" +
            "  const host = createCompilerHost(fileName, inputSourceCode, (data) => outputCode = data);\n" +
            "\n" +
            "  const program = ts.createProgram([fileName], options, host);\n" +
            "\n" +
            "  program.emit();\n" +
            "\n" +
            "  return outputCode;\n" +
            "}\n" +
            "function createCompilerHost(fileName, inputSourceCode, writeFile) {\n" +
            "  const sourceFile = ts.createSourceFile(fileName, inputSourceCode, ts.ScriptTarget.ES2020);\n" +
            "\n" +
            "  return {\n" +
            "    fileExists: (filePath) => filePath === fileName,\n" +
            "    directoryExists: (dirPath) => dirPath === '/',\n" +
            "    getCurrentDirectory: () => '/',\n" +
            "    getDirectories: () => [],\n" +
            "    getCanonicalFileName: (fileName) => fileName,\n" +
            "    getNewLine: () => '\\n',\n" +
            "    getDefaultLibFileName: () => '',\n" +
            "    getDefaultLibLocation: () => '',\n" +
            "\n" +
            "    getSourceFile: (filePath) => {\n" +
            "      if (filePath === fileName) {\n" +
            "        return sourceFile;\n" +
            "      } else {\n" +
            "        return ts.createSourceFile(filePath, getLibFileContent(filePath), ts.ScriptTarget.ES2020);\n" +
            "      }\n" +
            "    },\n" +
            "    readFile: (filePath) => filePath === fileName ? inputSourceCode : undefined,\n" +
            "    useCaseSensitiveFileNames: () => true,\n" +
            "    writeFile: (fileName, data) => writeFile(data),\n" +
            "  };\n" +
            "}\n";

    public static void main(String[] args) throws Exception {
        benchGraalPolyglotContext();
        benchGraalScriptEngine();
        benchNashornScriptEngine();
    }

    static long benchGraalPolyglotContext() throws IOException {
        System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");
        long sum = 0;
        try (Context context = Context.create()) {
            context.eval(Source.newBuilder("js", (new ResourceLoader()).loadReader("/typescript.js"),
                    "typescript.js").build());
            context.eval(Source.newBuilder("js", (new ResourceLoader()).loadReader("/libPack.js"),
                    "libPack.js").build());
            context.eval(Source.newBuilder("js", COMPILE_TS, "src.js").build());
            Value compileMain = context.getBindings("js").getMember("compileTypescript");

            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                compileMain.execute();
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                compileMain.execute();
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + took);
            }
        } // context.close() is automatic
        return sum;
    }

    static long benchNashornScriptEngine() throws IOException {
        System.out.println("=== Nashorn via javax.script.ScriptEngine ===");
        ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
        if (nashornEngine == null) {
            System.out.println("*** Nashorn not found ***");
            return 0;
        } else {
            return benchScriptEngineIntl(nashornEngine);
        }
    }

    static long benchGraalScriptEngine() throws IOException {
        System.out.println("=== Graal.js via javax.script.ScriptEngine ===");
        ScriptEngine graaljsEngine = new ScriptEngineManager().getEngineByName("graal.js");
        if (graaljsEngine == null) {
            System.out.println("*** Graal.js not found ***");
            return 0;
        } else {
            return benchScriptEngineIntl(graaljsEngine);
        }
    }

    private static long benchScriptEngineIntl(ScriptEngine eng) throws IOException {
        long sum = 0L;
        try {
            eng.eval((new ResourceLoader()).loadReader("/typescript.js"));
            eng.eval((new ResourceLoader()).loadReader("/libPack.js"));
            eng.eval(COMPILE_TS);
            Invocable inv = (Invocable) eng;
            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                inv.invokeFunction("compileTypescript");
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                inv.invokeFunction("compileTypescript");
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + (took));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return sum;
    }

}
