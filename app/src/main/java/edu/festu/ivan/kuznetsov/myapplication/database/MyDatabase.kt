package edu.festu.ivan.kuznetsov.myapplication.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.Auditorium
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.AuditoriumDAO
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.LessonDAO
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.database.route.RouteDAO
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.Teacher
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.TeacherDAO
import java.util.concurrent.Executors


@TypeConverters(Converters::class)
@Database(entities = [Auditorium::class, Route::class,Teacher::class,Lesson::class], version = 1, exportSchema = false)
abstract class MyDatabase: RoomDatabase() {
    abstract fun getRouteDAO(): RouteDAO
    abstract fun getLessonDAO(): LessonDAO
    abstract fun getAuditoriumDAO(): AuditoriumDAO
    abstract fun getTeacherDAO(): TeacherDAO
    //Double-checked singleton
    companion object {
        @Volatile
        private lateinit var instance: MyDatabase


        fun getInstance(context: Context): MyDatabase {
            if (! ::instance.isInitialized)
                synchronized(MyDatabase::class.java) {
                    if (! ::instance.isInitialized) {
                        //Применяется паттерн Builder
                        instance = Room
                            .databaseBuilder(context,MyDatabase::class.java, "my_database")
                            .addCallback(object : RoomDatabase.Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    val hold = Teacher("-","-","-")
                                    val rud = Teacher("Артём","Витальевич","Рудь")
                                    val tsai = Teacher("Вадим","Станиславович","Цай")
                                    val faleeva = Teacher("Елена","Валерьевна","Фалеева")
                                    val kholodilov = Teacher("Александр","Андреевич","Холодилов")
                                    val kuznetsov = Teacher("Иван","Владимирович","Кузнецов")
                                    Log.e("DATABASE ", "ID IS ${tsai.idTeacher} AND ${rud.idTeacher}")
                                    val audVR = Auditorium("428","Лаборатория VR/AR",89.83f, 18.28f, rud.idTeacher)
                                    val audGraph = Auditorium("426","Класс начертательной геометрии",89.83f, 30.04f, tsai.idTeacher)
                                    val audHead = Auditorium("441","Кабинет зав. кафедрой ВТиКГ",86.75f, 28.69f, faleeva.idTeacher)
                                    val audDepF = Auditorium("439","Кабинет кафедры ВТиКГ",86.75f, 37.08f, tsai.idTeacher)
                                    val audDepS = Auditorium("437","Кабинет кафедры ВТиКГ",86.75f, 46.28f, tsai.idTeacher)
                                    val audDepIT = Auditorium("437a","Кабинет кафедры ВТиКГ",86.75f, 51.42f, tsai.idTeacher)
                                    val audDepASP = Auditorium("435","Кабинет работы аспирантов ВТиКГ",76.67f, 65.36f, tsai.idTeacher)
                                    val audCompGraph = Auditorium("433","Класс компьютерной графики",58.75f, 65.36f, tsai.idTeacher)
                                    val audLectureF = Auditorium("431","Лекционная аудитория 1",55.33f, 65.36f, tsai.idTeacher)
                                    val audGraphPractice = Auditorium("422","Чертежный зал",55.33f, 65.36f, tsai.idTeacher)
                                    val audLectureS = Auditorium("420","Лекционная аудитория 1",55.33f, 65.36f, tsai.idTeacher)
                                    val audAdditiveTech = Auditorium("236","ССНКБ Аддитивных технологий",55.33f, 65.36f, kholodilov.idTeacher)
                                    val audIoT = Auditorium("3430","Лаборатория технологий Интернета Вещей",55.33f, 65.36f, kuznetsov.idTeacher)
                                    val s4 = Auditorium("Лестница №4","Лестница",86.75f, 63.6f, hold.idTeacher)
                                    Executors.newSingleThreadExecutor().execute {
                                        getInstance(context).getTeacherDAO().add(rud)
                                        getInstance(context).getTeacherDAO().add(tsai)
                                        getInstance(context).getTeacherDAO().add(faleeva)
                                        getInstance(context).getTeacherDAO().add(kholodilov)
                                        getInstance(context).getTeacherDAO().add(kuznetsov)
                                        getInstance(context).getTeacherDAO().add(hold)

                                        getInstance(context).getAuditoriumDAO().add(audVR)
                                        getInstance(context).getAuditoriumDAO().add(audGraph)
                                        getInstance(context).getAuditoriumDAO().add(audHead)
                                        getInstance(context).getAuditoriumDAO().add(audDepF)
                                        getInstance(context).getAuditoriumDAO().add(audDepS)
                                        getInstance(context).getAuditoriumDAO().add(audDepIT)
                                        getInstance(context).getAuditoriumDAO().add(audDepASP)
                                        getInstance(context).getAuditoriumDAO().add(audCompGraph)

                                        getInstance(context).getAuditoriumDAO().add(audLectureF)
                                        getInstance(context).getAuditoriumDAO().add(audGraphPractice)
                                        getInstance(context).getAuditoriumDAO().add(audLectureS)

                                        getInstance(context).getAuditoriumDAO().add(audAdditiveTech)
                                        getInstance(context).getAuditoriumDAO().add(audIoT)
                                        getInstance(context).getAuditoriumDAO().add(s4) }
                                }
                            })
                            .build()


                    }
                }
            return instance
        }
    }
}