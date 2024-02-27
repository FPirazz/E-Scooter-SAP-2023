package mvc;

import mvc.controller.MyController;
import mvc.controller.MyInputUI;
import mvc.model.MyModel;
import mvc.view.MyView;

public class AppMain {
    public static void main(String[] args) {
        MyModel model = new MyModel();
        MyView view = new MyView(model);
        MyInputUI inputUI = new MyInputUI();
        MyController controller = new MyController(model);
        inputUI.addObserver(controller);
        view.display();
        inputUI.display();
    }
}
