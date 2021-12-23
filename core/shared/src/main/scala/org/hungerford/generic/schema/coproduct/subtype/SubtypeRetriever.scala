package org.hungerford.generic.schema.coproduct.subtype

import org.hungerford.generic.schema.coproduct.subtype

import scala.util.NotGiven

trait SubtypeRetriever[ N <: TypeName, R <: Tuple ] {
    type Subtype

    def retrieve( from : R ) : Subtype
}

trait LowPrioritySubtypeRetrievers {
    given headDoesNotHaveName[ N <: TypeName, Head, Tail <: Tuple, Next ](
        using
        next : SubtypeRetriever.Aux[ N, Tail, Next ],
    ) : SubtypeRetriever.Aux[ N, Head *: Tail, Next ] = {
        new SubtypeRetriever[ N, Head *: Tail ] {
            type Subtype = next.Subtype

            override def retrieve( from : Head *: Tail ) : Next =
                next.retrieve( from.tail )
        }
    }
}

object SubtypeRetriever extends LowPrioritySubtypeRetrievers {
    type Aux[ N <: TypeName, R <: Tuple, ST ] = SubtypeRetriever[ N, R ] { type Subtype = ST }

    def apply[ N <: TypeName, R <: Tuple ](
        using
        str : SubtypeRetriever[ N, R ],
    ) : SubtypeRetriever.Aux[ N, R, str.Subtype ] = str

    given headHasName[ N <: TypeName, SubT, Tail <: Tuple ](
        using
        stn : SubtypeOfName[ N, SubT ],
    ) : SubtypeRetriever.Aux[ N, SubT *: Tail, SubT ] = {
        new SubtypeRetriever[ N, SubT *: Tail ] {
            type Subtype = SubT

            override def retrieve( from : SubT *: Tail ) : SubT = from.head
        }
    }

    def retrieve[ N <: TypeName, R <: Tuple ](
        typeName : N,
        subtypes : R,
    )(
        using
        fr : SubtypeRetriever[ N, R ],
    ) : fr.Subtype = fr.retrieve( subtypes )
}