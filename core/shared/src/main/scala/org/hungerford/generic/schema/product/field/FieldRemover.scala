package org.hungerford.generic.schema.product.field

import org.hungerford.generic.schema.product.ProductShape

trait FieldRemover[ N <: FieldName, R <: Tuple ] {
    type Out <: Tuple
  
    def remove( fields : R ) : Out
}

trait LowPriorityFieldRemovers {

    given nextFieldRemover[ N <: FieldName, Head, Tail <: Tuple, Next <: Tuple ](
        using
        next : FieldRemover.Aux[ N, Tail, Next ],
    ) : FieldRemover.Aux[ N, Head *: Tail, Head *: Next ] = new FieldRemover[ N, Head *: Tail ] {
        type Out = Head *: Next

        def remove( fields : Head *: Tail ) : Head *: Next =
            fields.head *: next.remove( fields.tail )
    }
}

object FieldRemover extends LowPriorityFieldRemovers {
    type Aux[ N <: FieldName, R <: Tuple, O <: Tuple ] =
        FieldRemover[ N, R ] { type Out = O }

    given fieldRemover[ T, N <: FieldName, S, Tail <: Tuple ] : FieldRemover.Aux[ N, FieldDescription.Aux[ T, N, S ] *: Tail, Tail ] = new FieldRemover[ N, FieldDescription.Aux[ T, N, S ] *: Tail ] {
        type Out = Tail

        def remove( fields : FieldDescription.Aux[ T, N, S ] *: Tail ) : Tail = fields.tail
    }

    def remove[ N <: FieldName, R <: Tuple, Res <: Tuple ](
        fieldName : N,
        from : R,
    )(
        using
        rm : FieldRemover[ N, R ],
    ) : rm.Out = rm.remove( from )
}

trait FieldReplacer[ N <: FieldName, R <: Tuple, NewT, NewN <: FieldName, NewS ] {
    type Out <: Tuple

    def replace( fields : R, withField : FieldDescription.Aux[ NewT, NewN, NewS ] ) : Out
}

trait LowPriorityFieldReplacers {
    given [ N <: FieldName, NewT, NewN <: FieldName, NewS, Head, Tail <: Tuple, Next <: Tuple ](
        using
        next : FieldReplacer.Aux[ N, Tail, NewT, NewN, NewS, Next  ]
    ) : FieldReplacer.Aux[ N, Head *: Tail, NewT, NewN, NewS, Head *: Next ] = {
        new FieldReplacer[ N, Head *: Tail, NewT, NewN, NewS ] {
            type Out = Head *: Next

            def replace(
                fields : Head *: Tail,
                withField : FieldDescription.Aux[ NewT, NewN, NewS ],
            ) : Head *: Next = fields.head *: next.replace( fields.tail, withField )
        }
    }
}

object FieldReplacer extends LowPriorityFieldReplacers {
    type Aux[ N <: FieldName, R <: Tuple, T, NewN <: FieldName, S, O <: Tuple ] =
        FieldReplacer[ N, R, T, NewN, S ] { type Out = O }
    
    given [ OldT, OldN <: FieldName, OldS, NewT, NewN <: FieldName, NewS, Tail <: Tuple ] : FieldReplacer.Aux[ OldN, FieldDescription.Aux[ OldT, OldN, OldS ] *: Tail, NewT, NewN, NewS, FieldDescription.Aux[ NewT, NewN, NewS ] *: Tail ] = {
            new FieldReplacer[  OldN, FieldDescription.Aux[ OldT, OldN, OldS ] *: Tail, NewT, NewN, NewS ] {
                type Out = FieldDescription.Aux[ NewT, NewN, NewS ] *: Tail

                def replace(
                    fields : FieldDescription.Aux[ OldT, OldN, OldS ] *: Tail,
                    withField : FieldDescription.Aux[ NewT, NewN, NewS ],
                ) : FieldDescription.Aux[ NewT, NewN, NewS ] *: Tail = withField *: fields.tail
            }
        }

    def replace[ N <: FieldName, R <: Tuple, NewT, NewN <: FieldName, NewS ](
        fieldName : N,
        from : R,
        withField : FieldDescription.Aux[ NewT, NewN, NewS ],
    )(
        using
        replacer : FieldReplacer[ N, R, NewT, NewN, NewS ],
    ) : replacer.Out = replacer.replace( from, withField )
}

trait FieldRetriever[ N <: FieldName, R <: Tuple ] {
    type Field

    def retrieve( from : R ) : Field
}

trait LowPriorityFieldRetrievers {
    given [ N <: FieldName, Head, Tail <: Tuple, Next ](
        using
        next : FieldRetriever.Aux[ N, Tail, Next ],
    ) : FieldRetriever.Aux[ N, Head *: Tail, Next ] = {
        new FieldRetriever[ N, Head *: Tail ] {
            type Field = next.Field

            override def retrieve( from : Head *: Tail ) : Next =
                next.retrieve( from.tail )
        }
    }
}

object FieldRetriever extends LowPriorityFieldRetrievers {
    type Aux[ N <: FieldName, R <: Tuple, F ] =
        FieldRetriever[ N, R ] { type Field = F }

    given [ N <: FieldName, T, S, Tail <: Tuple ] :
      FieldRetriever.Aux[ N, FieldDescription.Aux[ T, N, S ] *: Tail, FieldDescription.Aux[ T, N, S ] ] = {
        new FieldRetriever[ N, FieldDescription.Aux[ T, N, S ] *: Tail ] {
            type Field = FieldDescription.Aux[ T, N, S ]

            override def retrieve( from : FieldDescription.Aux[ T, N, S ] *: Tail ) : FieldDescription.Aux[ T, N, S ] =
                from.head
        }
    }

    def retrieve[ N <: FieldName, R <: Tuple ](
        fieldName : N,
        fields : R,
    )(
        using
        fr : FieldRetriever[ N, R ],
    ) : fr.Field = fr.retrieve( fields )
}
