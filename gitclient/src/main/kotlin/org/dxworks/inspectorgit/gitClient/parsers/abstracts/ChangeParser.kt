                isBinary = lines.any { it.startsWith("Binary files") })
                if (!it.startsWith("\\"))
                    currentHunkLines.add(it)