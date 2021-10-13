from distutils.core import setup
from distutils.extension import Extension
from Cython.Distutils import build_ext

ext_modules = [
    Extension("montecarlo", ["src/montecarlo/__init__.py"]
	# ["src/montecarlo/__init__.py","src/montecarlo/bestmovestrategies.py", "src/montecarlo/montecarlo_.py", "src/montecarlo/node.py", "src/montecarlo/scorestrategies.py", "src/montecarlo/stats.py"]
	),
    # Extension("mymodule2", ["mymodule2.py"]),  # ... all your modules that need be compiled ...
]

setup(
    name='My Program Name',
    cmdclass={'build_ext': build_ext},
    ext_modules=ext_modules
)
