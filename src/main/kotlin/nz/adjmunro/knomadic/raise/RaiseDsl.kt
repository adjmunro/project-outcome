package nz.adjmunro.knomadic.raise

/**
 * Annotation marking members of the [Outcome] DSL.
 */
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
)
@DslMarker
public annotation class RaiseDsl
