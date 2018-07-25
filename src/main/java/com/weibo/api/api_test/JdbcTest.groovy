package com.weibo.api.api_test

import cn.sina.api.commons.thead.TraceableThreadExecutor

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class JdbcTest {
    public static final ThreadPoolExecutor SQL_INSERT_POOL = new TraceableThreadExecutor(200, 200, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(300), new ThreadPoolExecutor.DiscardOldestPolicy())

    void insertTest() {
        final CountDownLatch countDownLatch = new CountDownLatch(200)
        for (int i = 0; i < 200; i++) {
            SQL_INSERT_POOL.submit(new Runnable() {
                @Override
                public void run() {
                    String driver = "com.mysql.jdbc.Driver"
                    String url = "jdbc:mysql://127.0.0.1:3306/test"
                    String user = "root"
                    String password = ""
                    try {
                        Class.forName(driver)
                        Connection conn = DriverManager.getConnection(url, user, password)
                        if (!conn.isClosed())
                            println("Succeeded connecting to the Database!")

                        Statement statement = conn.createStatement()
                        String sql = "insert into test (mid, min_cmt_root_id, count) values(4144384825947820, 4144384825947820, 1) on duplicate key update count=greatest(0, cast(count as signed)+1)"
                        for (int j = 0; j < 200; j++) {
                            statement.executeUpdate(sql)
                        }
                        conn.close()
                    } catch(ClassNotFoundException e) {
                        System.out.println("Sorry,can`t find the Driver!")
                        e.printStackTrace()
                    } catch(Exception e) {
                        e.printStackTrace()
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            })
        }
        countDownLatch.await()
        System.out.println("done")
    }
    public static void main(def args) {
        insertTest()
    }
}
