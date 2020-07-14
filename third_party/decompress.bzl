# Build rules to decompress files

def _unxz_impl(ctx):
    output_files = []
    for f in ctx.files.input:
        if not f.basename.endswith(".xz"):
            fail("Unexpected file extension " + f.extension, "input")
        output_filename = f.basename[:-3]
        output_file = ctx.actions.declare_file(output_filename, sibling = f)
        ctx.actions.run_shell(
            outputs = [output_file],
            inputs = [f],
            mnemonic = "unxz",
            command = 'unxz --stdout "$1" > "$2"',
            arguments = [f.path, output_file.path],
        )
        output_files.append(output_file)
    return [DefaultInfo(files = depset(output_files))]

unxz = rule(
    implementation = _unxz_impl,
    attrs = {
        "input": attr.label(
            doc = "The input file",
            allow_files = [".xz"],
            mandatory = True,
        ),
    },
)
