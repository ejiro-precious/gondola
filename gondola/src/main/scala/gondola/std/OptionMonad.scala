package gondola.std

import cats._

import scala.annotation.tailrec

/**
  * Created by jamie.pullar on 06/06/2016.
  */
trait OptionMonad {

  implicit val optionMonad:Monad[Option] =
    new Monad[Option] {
      def pure[A](x: A): Option[A] =
        Some(x)

      def flatMap[A, B](fa: Option[A])(f: (A) => Option[B]): Option[B] =
        fa.flatMap(f)

      @tailrec
      def tailRecM[A, B](a: A)(f: (A) => Option[Either[A, B]]): Option[B] =
        f(a) match {
          case Some(Left(aa)) => tailRecM(aa)(f)
          case Some(Right(b)) => Some(b)
          case _ => None
        }
    }

  implicit val optionTraverse:Traverse[Option] = new Traverse[Option] {
    def traverse[G[_], A, B](fa: Option[A])(f: (A) => G[B])(implicit A: Applicative[G]): G[Option[B]] =
      fa.map(f).fold[G[Option[B]]](A.pure(None))(gb => A.map(gb)(Some.apply))

    def foldLeft[A, B](fa: Option[A], b: B)(f: (B, A) => B): B =
      fa.foldLeft(b)(f)

    def foldRight[A, B](fa: Option[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
      fa.foldRight(lb)(f)
  }
}

object OptionMonad extends OptionMonad
