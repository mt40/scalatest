/*
 * Copyright 2001-2015 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalactic

trait LazyBag[+T] {
  def map[U](f: T => U): LazyBag[U]
  def flatMap[U](f: T => LazyBag[U]): LazyBag[U]
  def toEquaSet[U >: T](toPath: EquaPath[U]): toPath.EquaSet
  def toSortedEquaSet[U >: T](toPath: SortedEquaPath[U]): toPath.SortedEquaSet
  def toList: List[T]
  def size: Int
}

object LazyBag {
  private class BasicLazyBag[T](private val args: List[T]) extends LazyBag[T] { thisLazyBag =>
    def map[U](f: T => U): LazyBag[U] = new MappedLazyBag(thisLazyBag, f)
    def flatMap[U](f: T => LazyBag[U]): LazyBag[U] = new FlatMappedLazyBag(thisLazyBag, f)
    def toEquaSet[U >: T](toPath: EquaPath[U]): toPath.FastEquaSet = toPath.FastEquaSet(args: _*)
    def toSortedEquaSet[U >: T](toPath: SortedEquaPath[U]): toPath.SortedEquaSet = ???
    def toList: List[T] = args
    def size: Int = args.size
    override def toString = args.mkString("LazyBag(", ",", ")")
/*  // Don't uncomment unless have a failing test
    override def equals(other: Any): Boolean =
      other match {
        case otherLazyBag: LazyBag[_] => 
          thisLazyBag.toList.groupBy(o => o) == otherLazyBag.toList.groupBy(o => o)
        case _ => false
      }
    override def hashCode: Int = thisLazyBag.toList.groupBy(o => o).hashCode
*/
  }

  private class MappedLazyBag[T, U](lazyBag: LazyBag[T], f: T => U) extends LazyBag[U] { thisLazyBag => 
    def map[V](g: U => V): LazyBag[V] = new MappedLazyBag[T, V](lazyBag, f andThen g)
    def flatMap[V](f: U => LazyBag[V]): LazyBag[V] = ???
    def toEquaSet[V >: U](toPath: EquaPath[V]): toPath.FastEquaSet = {
      toPath.FastEquaSet(toList: _*)
    }
    def toSortedEquaSet[V >: U](toPath: SortedEquaPath[V]): toPath.SortedEquaSet = ???
    def toList: List[U] = lazyBag.toList.map(f)
    def size: Int = toList.size
    override def toString: String = toList.mkString("LazyBag(", ",", ")")
    override def equals(other: Any): Boolean =
      other match {
        case otherLazyBag: LazyBag[_] => 
          thisLazyBag.toList.groupBy(o => o) == otherLazyBag.toList.groupBy(o => o)
        case _ => false
      }
    override def hashCode: Int = thisLazyBag.toList.groupBy(o => o).hashCode
  }

  private class FlatMappedLazyBag[T, U](lazyBag: LazyBag[T], f: T => LazyBag[U]) extends LazyBag[U] { thisLazyBag => 
    def map[V](g: U => V): LazyBag[V] = new MappedLazyBag[U, V](thisLazyBag, g)
    def flatMap[V](f: U => LazyBag[V]): LazyBag[V] = ???
    def toEquaSet[V >: U](toPath: EquaPath[V]): toPath.FastEquaSet = {
      toPath.FastEquaSet(toList: _*)
    }
    def toSortedEquaSet[V >: U](toPath: SortedEquaPath[V]): toPath.SortedEquaSet = ???
    def toList: List[U] = lazyBag.toList.flatMap(f.andThen(_.toList))
    def size: Int = toList.size
    override def toString: String = toList.mkString("LazyBag(", ",", ")")
    override def equals(other: Any): Boolean = ???
    override def hashCode: Int = ???
  }
  
  def apply[T](args: T*): LazyBag[T] = new BasicLazyBag(args.toList)
}

