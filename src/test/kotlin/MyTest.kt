import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MyTest {

    @Test
    fun test() {

        val kotlinSource = SourceFile.kotlin(
            "Main.kt", """
            import MyClass.duplicate
            import java.util.Optional
            
            object MyClass
            
            fun test(): List<String> {
                return listOf("abc").duplicate
            }
        
    """
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)

            // pass your own instance of a compiler plugin
            compilerPlugins = listOf(MyComponentRegistrar())

            useIR = true
            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }

        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.INTERNAL_ERROR, result.exitCode)
        assertTrue(result.messages.contains("MyException"), "Error message contains mention of ${MyException::class.java}")

    }

}