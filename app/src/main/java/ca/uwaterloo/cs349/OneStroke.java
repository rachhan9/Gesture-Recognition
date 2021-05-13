package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class OneStroke implements Serializable {

    class MyPoint extends Point implements Serializable {
        MyPoint(int x, int y){
            super(x,y);
        }
        private void writeObject(ObjectOutputStream outputStream) throws IOException {
            outputStream.defaultWriteObject();
            outputStream.writeObject(this.x);
            outputStream.writeObject(this.y);
        }

        private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
            inputStream.defaultReadObject();
            int x = (int)inputStream.readObject();
            int y = (int) inputStream.readObject();
            this.x = x;
            this.y = y;
        }

    }

    ArrayList<MyPoint> originalPoints;
    transient ArrayList<MyPoint> transformPoints;
    MyPoint firstPoint;
    transient  Point centroid = null;

    String strokeName = "";
    transient Bitmap bitmap;

    OneStroke(){
    }

    OneStroke(int x,int y){
        originalPoints = new ArrayList<MyPoint>();
        firstPoint = new MyPoint(x,y);
        originalPoints.add(new MyPoint(0,0));


    }


    void addPoint(int x, int y){
        assert (firstPoint != null);
        // x and y are mouse position
        x -= firstPoint.x;
        y -= firstPoint.y;
        originalPoints.add(new MyPoint(x,y));
    }

    void draw(Canvas canvas){
        Paint paint = new Paint(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        Point basePoint = firstPoint;

        // actual draw
        Path path = new Path();
        path.moveTo(originalPoints.get(0).x + basePoint.x, originalPoints.get(0).y + basePoint.y);
        canvas.drawCircle(originalPoints.get(0).x + basePoint.x, originalPoints.get(0).y + basePoint.y,5,paint);
        for(int i = 1; i < originalPoints.size(); i++){
            path.lineTo(originalPoints.get(i).x + basePoint.x, originalPoints.get(i).y + basePoint.y);
        }

        canvas.drawPath(path,paint);




//        // debug draw
//        ArrayList<MyPoint> points = originalPoints;
//
//        if (centroid != null){
//            basePoint = centroid;
//        }
//        if (transformPoints != null){
//            points = transformPoints;
//        }
//        for (Point p : points) {
//            canvas.drawCircle(p.x + basePoint.x, p.y + basePoint.y, 5, paint);
//        }
//        canvas.drawCircle(basePoint.x,basePoint.y,10,paint);

    }



    public void copyOriginToTransform(){
        Log.d(getClass().getSimpleName(),"Copy");
        this.transformPoints = new ArrayList<>();
        for(Point p: this.originalPoints){
            this.transformPoints.add(new MyPoint(p.x,p.y));
        }
    }

    void setCentroid(){
        Log.d(getClass().getSimpleName(),"SetCentroid");
        int n = transformPoints.size();
        int x = 0, y = 0;
        for(Point p : transformPoints){
            x += p.x;
            y += p.y;
        }
        Point cen =  new Point(x/n,y/n);



        // Adjust all point around centroid:
        for (Point p : transformPoints) {
            p.x += (-cen.x);
            p.y += (-cen.y);
        }
        centroid = new Point(0,0);
        centroid.x = firstPoint.x + (cen.x);
        centroid.y = firstPoint.y + (cen.y);

    }


    void rotate(){
        Log.d(getClass().getSimpleName(),"Rotate");
        setCentroid();
        int n = transformPoints.size();
        int x = 0, y = 0;
        for(Point p : transformPoints){
            x += p.x;
            y += p.y;
        }
        Point cen =  new Point(x/n,y/n);
        assert (cen.x == 0);
        assert (cen.y == 0);

        double angle = Math.atan2(0,distance(new Point(0,0), transformPoints.get(0))) -  Math.atan2 ( transformPoints.get(0).y , transformPoints.get(0).x);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        for (Point p : transformPoints){
            p.x -= cen.x;
            p.y -= cen.y;
            double newX =  p.x * cos - p.y * sin;
            double newY =  p.x * sin + p.y * cos;
            p.x = (int)(cen.x + newX);
            p.y = (int)(cen.y + newY);

        }

    }

    static double distance (Point p1, Point p2){
        return Math.sqrt(Math.pow(p1.x - p2.x,2)
                + Math.pow(p1.y - p2.y,2));
    }

    void resample(int N){
        Log.d(getClass().getSimpleName(),"Resample");
        double total_dist = 0;
        for(int i = 0; i < transformPoints.size() - 1; i++){
            total_dist += distance(transformPoints.get(i), transformPoints.get(i+1));
        }

        int interval_length = (int)(total_dist / (N - 1));
        double cummuDist = 0;
        ArrayList<MyPoint> resampledPoints = new ArrayList<>();
        resampledPoints.add(transformPoints.get(0));

        int i = 0;
        while(resampledPoints.size() < N && i < transformPoints.size() - 1){
            double dist = distance(transformPoints.get(i), transformPoints.get(i+1));
            if (cummuDist + dist >= interval_length){

                double ratio = (interval_length - cummuDist) / dist;
                int del_x = transformPoints.get(i+1).x - transformPoints.get(i).x;
                int del_y = transformPoints.get(i+1).y - transformPoints.get(i).y;
                MyPoint p = new MyPoint((int)(transformPoints.get(i).x + ratio * del_x), (int)(transformPoints.get(i).y + ratio * del_y));
                resampledPoints.add(p);
                transformPoints.add(i+1,p);
                cummuDist = 0;

            } else {

                cummuDist += dist;

            }
            i += 1;
        }
        if (resampledPoints.size() < N){
            resampledPoints.add(transformPoints.get(transformPoints.size()-1));
        }
        transformPoints = resampledPoints;

    }


    void scale(){
        Log.d(getClass().getSimpleName(),"Scale");
        double box_width = 100;

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY,  maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Point p : transformPoints){
            minX = Math.min(p.x,minX);
            minY = Math.min(p.y,minY);
            maxX = Math.max(p.x,maxX);
            maxY = Math.max(p.y,maxY);
        }
        double ratio_wid = box_width / (maxX - minX) ;
        double ratio_hei = box_width/ (maxY - minY);


        for (Point p : transformPoints){
            p.x = (int)(p.x * ratio_wid);
            p.y = (int)(p.y * ratio_hei);
        }

    }

    static double computeScore(OneStroke s1, OneStroke s2){
        int N = Math.min(s1.transformPoints.size(), s2.transformPoints.size());
        double score = 0;
        for(int i = 0; i < N; i++){
            score += distance(s1.transformPoints.get(i),s2.transformPoints.get(i));
        }
        score = score / N;

        return score;
    }


    void save(){

        this.copyOriginToTransform();
        this.setCentroid();

        // create bit map from original points
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY,  maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Point p : transformPoints){
            minX = Math.min(p.x,minX);
            minY = Math.min(p.y,minY);
            maxX = Math.max(p.x,maxX);
            maxY = Math.max(p.y,maxY);
        }
        int wid = (int)((maxX - minX) * 1.5 + 30);
        int hei = (int)((maxY - minY) * 1.5) + 30;
        bitmap = Bitmap.createBitmap(wid,hei, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);


        Paint paint = new Paint(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        Point basePoint = new Point(wid/2,hei/2);
        Path path = new Path();
        path.moveTo(transformPoints.get(0).x + basePoint.x, transformPoints.get(0).y + basePoint.y);
        canvas.drawCircle(transformPoints.get(0).x + basePoint.x, transformPoints.get(0).y + basePoint.y,10,paint);
        for(int i = 1; i < transformPoints.size(); i++){
            path.lineTo(transformPoints.get(i).x + basePoint.x, transformPoints.get(i).y + basePoint.y);
            assert(transformPoints.get(i).x + basePoint.x >= 0);
            assert(transformPoints.get(i).x + basePoint.x <= wid);
            assert(transformPoints.get(i).y + basePoint.y >= 0);
            assert(transformPoints.get(i).y + basePoint.y <= hei);
        }
        canvas.drawPath(path,paint);


        this.resample(128);
        this.setCentroid();
        this.rotate();
        this.scale();
    }

    @Override
    public Object clone() {
        OneStroke stroke = new OneStroke();
        stroke.originalPoints = new ArrayList<MyPoint>();
        for(Point p: this.originalPoints){
            stroke.originalPoints.add(new MyPoint(p.x,p.y));
        }
        stroke.transformPoints = new ArrayList<MyPoint>();
        for(Point p: this.transformPoints){
            stroke.transformPoints.add(new MyPoint(p.x,p.y));
        }
        stroke.strokeName = this.strokeName;
        stroke.centroid = new Point( this.centroid.x, this.centroid.y);
        stroke.firstPoint = new MyPoint(this.firstPoint.x,this.firstPoint.y);
        stroke.bitmap = this.bitmap.copy(this.bitmap.getConfig(),true);

        return stroke;
    }



    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        this.save();

    }
    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
    }


    public static byte[] convertCurrStateToByte() {
        byte[] ret = null;
        try {

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(bo);
            o.writeObject(SharedViewModel.savedStrokes);
            o.close();
            bo.close();

            ret = bo.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static void loadCurrStatefromByte(byte[] content)throws ClassNotFoundException{
        try {

            ByteArrayInputStream bi = new ByteArrayInputStream(content);
            ObjectInputStream oi = new ObjectInputStream(bi);

            SharedViewModel.savedStrokes = (ArrayList<OneStroke>) oi.readObject();

            oi.close();
            bi.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
