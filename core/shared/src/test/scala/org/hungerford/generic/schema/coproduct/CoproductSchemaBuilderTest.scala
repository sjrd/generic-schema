package org.hungerford.generic.schema.coproduct

import org.hungerford.generic.schema.{Schema, Primitive}
import org.hungerford.generic.schema.coproduct.subtype.{Subtype, SubtypeCase}
import org.scalatest.flatspec.AnyFlatSpecLike

import org.hungerford.generic.schema.Default.dsl.*

class CoproductSchemaBuilderTest extends AnyFlatSpecLike with org.scalatest.matchers.should.Matchers {

    behavior of "CoproductSchemaBuilder"

    case class SomeTrait( value : Any )

    it should "be able to add a subtype" in {
        val csb = Schema.coproductBuilder[ SomeTrait ]
          .buildSubtype[ Int ]( _.typeName( "int" ).primitive.asSuper( SomeTrait.apply ).build )
          .buildSubtype[ String ]( _.typeName( "str" ).primitive.asSuper( SomeTrait.apply ).build )
          .build

        summon[ csb.type <:< Schema.Aux[ SomeTrait, CoproductShape[ SomeTrait, (Subtype.Aux[ SomeTrait, Int, Unit, Nothing, Unit, "int",  Unit ], Subtype.Aux[ SomeTrait, String, Unit, Nothing, Unit, "str", Unit ]), (Int, String), Unit, Nothing ] ] ]
    }

    trait SuperTrait
    case class SubCase( int : Int ) extends SuperTrait

    it should "be able to add real subtype" in {
        val csb = Schema.coproductBuilder[ SuperTrait ]
          .buildSubtype[ SubCase ]( _.typeName( "sub-case" ).fromSchema( Schema.derived ).build )
          .build
    }

    case class SubCase2( int : Int ) extends SuperTrait

    it should "not be able to add two subtypes with the same name" in {
        val csb = Schema.coproductBuilder[ SuperTrait ]
          .buildSubtype[ SubCase ]( _.typeName( "sub-case" ).fromSchema( Schema.derived ).build )

        assertDoesNotCompile(
            """csb.buildSubtype[ SubCase2 ]( _.typeName( "sub-case" )
              |.fromSchema( Schema.derived ).build )""".stripMargin,
        )

        assertCompiles(
            """csb.buildSubtype[ SubCase2 ]( _.typeName( "sub-case-2" )
              |.fromSchema( Schema.derived ).build )""".stripMargin,
        )
    }

    it should "not be able to add two subtypes with same discriminator value" in {
        val csb = Schema.coproductBuilder[ SuperTrait ]
          .discriminator[ Int ]( "int" )
          .buildSubtype[ SubCase ]( _.typeName( "sub-case" ).fromSchema( Schema.derived ).discriminatorValue( 1 ).build )

        assertDoesNotCompile(
            """csb.buildSubtype[ SubCase2 ]( _.typeName( "sub-case-2" ).discriminatorValue( 1 )
              |.fromSchema( Schema.derived ).build )""".stripMargin,
        )

        assertCompiles(
            """csb.buildSubtype[ SubCase2 ]( _.typeName( "sub-case-2" ).discriminatorValue( 2 )
              |.fromSchema( Schema.derived ).build )""".stripMargin,
        )

    }

}