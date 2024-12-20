// Пример кода
var foo = 112
var bar = 226

bar = foo + bar

func func_bar(arg2: Double) {
    var func_bar_quux = arg2 / arg2
}

func func_foo(arg0: Int, arg1: Int) {
    let func_foo_baz: Double = (arg0 + arg1 + bar) * 1.0
    func_bar(arg2: func_foo_baz)
}

func_foo(arg0: bar, arg1: 100)

let quux = bar / 1.0
func_bar(arg2: quux)