from flask import Flask
from application import db
from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from config import query_yaml
from verify_identity import verify_identity
from application import bp

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = query_yaml('db.MYSQL')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
for bluep in bp:
    app.register_blueprint(bluep)
app.before_request(verify_identity)

db.init_app(app)
manager = Manager(app)
migrate = Migrate(app, db)

manager.add_command('db', MigrateCommand)

if __name__ == '__main__':
    manager.run()
