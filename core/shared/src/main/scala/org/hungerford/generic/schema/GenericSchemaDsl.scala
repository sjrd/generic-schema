package org.hungerford.generic.schema

import org.hungerford.generic.schema.coproduct.subtype.{Subtype, SubtypeDsl, TypeName}
import org.hungerford.generic.schema.selector.SelectorDsl
import org.hungerford.generic.schema.product.field.{FieldDsl, FieldName}

class GenericSchemaDsl( conf : SchemaConfiguration )
    extends SchemaDsl
      with FieldDsl
      with SubtypeDsl
      with SelectorDsl {

    given SchemaConfiguration = conf

    val Schema = org.hungerford.generic.schema.Schema
    type Schema[ T ] = org.hungerford.generic.schema.Schema[ T ]

    val Field = org.hungerford.generic.schema.product.field.Field
    type Field[ T, F, N <: FieldName, S ] = org.hungerford.generic.schema.product.field.Field[ T, F, N, S ]

    val Subtype = org.hungerford.generic.schema.coproduct.subtype.Subtype
    type Subtype[ T, ST, D, DN, DV, N <: TypeName, S ] = org.hungerford.generic.schema.coproduct.subtype.Subtype[ T, ST, D, DN, DV, N, S ]

    val Validator = org.hungerford.generic.schema.validator.Validator
    type Validator[ T ] = org.hungerford.generic.schema.validator.Validator[T]
}
