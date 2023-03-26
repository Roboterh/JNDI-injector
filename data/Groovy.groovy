@groovy.transform.ASTTest(value={
    assert java.lang.Runtime.getRuntime().exec("calc")
})
def x