package org.hungerford.generic.schema.product.field

import org.hungerford.generic.schema.SchemaBuilder
import org.hungerford.generic.schema.product.CtxWrapTuplesConstraint
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class FieldReplacerTest extends AnyFlatSpecLike with Matchers {

    behavior of "FieldReplacer"

    case class TestClass( int : Int, str : String, bool : Boolean )

    val fields = SchemaBuilder[ TestClass ]
      .caseClass
      .build
      .shape
      .fieldDescriptions

    it should "replace the first field in a tuple of field descriptions" in {
        val newField = FieldDescriptionBuilder[ Char ]
          .fieldName( "integer" )
          .primitive
          .build

        val newFields = FieldReplacer.replace( "int", fields, newField )

        newFields.size shouldBe 3
        newFields.head.fieldName shouldBe "integer"
        newFields.tail.head.fieldName shouldBe "str"
        newFields.tail.tail.head.fieldName shouldBe "bool"
    }

    it should "replace the second field in a tuple of field descriptions" in {
        val newField = FieldDescriptionBuilder[ Char ]
          .fieldName( "string" )
          .primitive
          .build

        val newFields = FieldReplacer.replace( "str", fields, newField )

        newFields.size shouldBe 3
        newFields.head.fieldName shouldBe "int"
        newFields.tail.head.fieldName shouldBe "string"
        newFields.tail.tail.head.fieldName shouldBe "bool"
    }

    it should "replace the last field in a tuple of field descriptions" in {
        val newField = FieldDescriptionBuilder[ Char ]
          .fieldName( "boolean" )
          .primitive
          .build

        val newFields = FieldReplacer.replace( "bool", fields, newField )

        newFields.size shouldBe 3
        newFields.head.fieldName shouldBe "int"
        newFields.tail.head.fieldName shouldBe "str"
        newFields.tail.tail.head.fieldName shouldBe "boolean"
    }

}