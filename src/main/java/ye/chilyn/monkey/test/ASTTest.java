package ye.chilyn.monkey.test;

import static ye.chilyn.monkey.Printer.println;

import ye.chilyn.monkey.Token;
import ye.chilyn.monkey.TokenType;
import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.Program;

public class ASTTest {
   public void testString() {
      Program program = new Program();
      program.statements.add(new LetStatement(
              new Token(TokenType.LET, "let"),
              new Identifier(
                      new Token(TokenType.IDENT, "myVar"),
                      "myVar"
              ),
              new Identifier(
                      new Token(TokenType.IDENT, "anotherVar"),
                      "anotherVar"
              )

      ));

      if (!"let myVar = anotherVar;".equals(program.string())) {
         println("program.String() wrong. got=" + program.string());
         return;
      }
      println("success");
   }
}
