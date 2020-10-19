package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.myapplication.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("select * from Crime")
    fun getCrimes():LiveData<List<Crime>>;

    @Query("select * from Crime where id=(:id) ")
    fun getCrime(id:UUID):LiveData<Crime?>;
}