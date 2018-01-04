package com.thoughtworks.expressions

import com.dongxiguo.fastring.Fastring
import com.dongxiguo.fastring.Fastring.Implicits._
import shapeless.{Nat, Sized, Succ}

import scala.language.higherKinds

/**
  * @author 杨博 (Yang Bo)
  */
trait OpenCLPointerExpressions extends PointerExpressions with OpenCLBooleanExpressions {

  protected trait TypeApi extends super[PointerExpressions].TypeApi with super[OpenCLBooleanExpressions].TypeApi {
    this: Type =>

  }

  type Type <: (Expression with Any) with TypeApi
  trait ValueTypeApi extends TypeApi with super.ValueTypeApi { elementType: ValueType =>

    protected trait PointerTypeApi[NumberOfDimensions <: Nat]
        extends super.PointerTypeApi[NumberOfDimensions]
        with TypeApi { pointerType: PointerType[NumberOfDimensions] =>

      type AffineTransform = Sized[Seq[Sized[Double, Succ[NumberOfDimensions]]], NumberOfDimensions]

      override def toCode(context: Context): Type.Code = {
        val element = context.get(elementType)
        Type.Code(
          globalDeclarations = Fastring.empty,
          globalDefinitions = fast"typedef global ${element.packed} * $name;",
          Type.Accessor.Atom(name)
        )

      }

      protected trait TypedTermApi extends super[TypeApi].TypedTermApi with super[PointerTypeApi].TypedTermApi {
        this: TypedTerm =>
      }

      type TypedTerm <: (Term with Any) with TypedTermApi

      protected trait DereferenceApi extends super.DereferenceApi with elementType.TypedTermApi {
        this: elementType.TypedTerm =>
        def toCode(context: Context): Term.Code = ???
      }

      type Dereference <: (elementType.TypedTerm with Any) with DereferenceApi

    }

    type PointerType[NumberOfDimensions <: Nat] <: (Type with Any) with PointerTypeApi[NumberOfDimensions]

  }
  type ValueType <: (Type with Any) with ValueTypeApi

}
