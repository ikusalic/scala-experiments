object CallByNameVsFunctionAsArgument {
  val start = System.currentTimeMillis

  def testTakeFunction() = {
    takesFunction(f, "no temp variables")

    val fv: () => Long = f
    takesFunction(fv, "using temp val with explicit type, no type mismatch")
    lazy val flv: () => Long = f
    takesFunction(flv, "using temp lazy val with explicit type, no type mismatch")

    //    val v2 = f
    //    takesFunction(v2, "would cause a type mismatch")
    //    lazy val lv2 = f
    //    takesFunction(lv2, "would cause a type mismatch")
    println("// val tmp = f; takesFunction(tmp) would cause a type mismatch")
  }

  def testCallByName() = {
    callByName(f, "no temp variables")

    val v = f
    callByName(v, "using temp val")

    lazy val lv = f
    val valueAtCallTime = f
    Thread.sleep(100)
    callByName(lv, s"!= $valueAtCallTime (value at call time); using temp lazy val")
  }

  def f() = System.currentTimeMillis() - start

  def callByName(arg: => Long, comment: String = "") = {
    val a = arg  // parenthesis absent
    Thread.sleep(100)
    val b = arg
    println(s"callByName: $a ${ if(a == b) "==" else "!=" } $b:   // $comment")
  }

  def takesFunction(arg: () => Long, comment: String = "") = {
    val a = arg()  // parenthesis present
    Thread.sleep(100)
    val b = arg()
    println(s"takesFunction: $a ${ if(a == b) "==" else "!=" } $b:   // $comment")
  }

  def sameName(arg: Long) = ???
  def sameName(arg: () => Long) = ???
  // when trying to overload:
  // def sameName(arg: => Long) = ???
  //
  // Error: double definition:
  //    method sameName:(arg: => Long)Nothing and
  //    method sameName:(arg: () => Long)Nothing at line 38
  // have same type after erasure: (arg: Function0)Nothing

  def tryLocalOverloading() = {
    def sameName(arg: () => Long) = ???
    // when trying to overload with completely unrelated type:
    // def sameName(arg: String) = ???
    // Error: method sameName is defined twice

    println("local overloading: fails even for completely unrelated types")
  }

  def main(args: Array[String]) {
    testTakeFunction()
    println
    testCallByName()

    println("\nglobal overloading fails for arguments of types: '() => Long' and '=> Long' because they the have same erasure")
    tryLocalOverloading()
  }
}
