/* eslint-disable react/prop-types */
import React, { useState } from "react";
import { Button, Col, Form, Row } from "react-bootstrap";
import { Controller, useForm } from "react-hook-form";
import { colasServices } from "../services/colas.service";
import { useNavigate } from "react-router-dom";

const DataForm = ({ setRespuestas }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    control,
  } = useForm({
    defaultValues: {
      porcentaje_aprendiz: 15.0,
      tiempo_atencion_min_aprendiz: 20.0,
      tiempo_atencion_max_aprendiz: 30.0,
      porcentaje_veterano_a: 45.0,
      tiempo_atencion_min_veterano_a: 11.0,
      tiempo_atencion_max_veterano_a: 13.0,
      porcentaje_veterano_b: 40.0,
      tiempo_atencion_min_veterano_b: 12.0,
      tiempo_atencion_max_veterano_b: 18.0,
      tiempo_llegada_min: 2.0,
      tiempo_llegada_max: 12.0,
      cantidad_dias: "",
      hora_desde: "",
      dia_desde: "",
      cantidad_iteraciones: "",
    },
  });

  const navigate = useNavigate();

  const [errorAPI, setErrorApi] = useState("");

  const onSubmit = async (data) => {
    data.porcentaje_aprendiz = parseFloat(data.porcentaje_aprendiz);
    data.tiempo_atencion_min_aprendiz = parseFloat(
      data.tiempo_atencion_min_aprendiz
    );
    data.tiempo_atencion_max_aprendiz = parseFloat(
      data.tiempo_atencion_max_aprendiz
    );
    data.porcentaje_veterano_a = parseFloat(data.porcentaje_veterano_a);
    data.tiempo_atencion_min_veterano_a = parseFloat(
      data.tiempo_atencion_min_veterano_a
    );
    data.tiempo_atencion_max_veterano_a = parseFloat(
      data.tiempo_atencion_max_veterano_a
    );
    data.porcentaje_veterano_b = parseFloat(data.porcentaje_veterano_b);
    data.tiempo_atencion_min_veterano_b = parseFloat(
      data.tiempo_atencion_min_veterano_b
    );
    data.tiempo_atencion_max_veterano_b = parseFloat(
      data.tiempo_atencion_max_veterano_b
    );
    data.tiempo_llegada_min = parseFloat(data.tiempo_llegada_min);
    data.tiempo_llegada_max = parseFloat(data.tiempo_llegada_max);

    data.cantidad_dias = parseInt(data.cantidad_dias);
    data.hora_desde = parseInt(data.hora_desde);
    data.dia_desde = parseInt(data.dia_desde);
    data.cantidad_iteraciones = parseInt(data.cantidad_iteraciones);
    console.log(data);
    const response = await colasServices.simular(data);

    if (typeof response === "string") {
      setErrorApi(response);
      return;
    }
    setErrorApi("");

    console.log("rta de api: ", response);
    setRespuestas(response);
    navigate("/table");
  };

  return (
    <>
      <div className="card">
        <h3>Peluqueros</h3>

        <Row className="align-items-start">
          <Col>
            <h4 className="text-center">Aprendiz</h4>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_aprendiz"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_aprendiz.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_min_aprendiz"
                  control={control}
                  rules={{
                    value: {
                      value: 20,
                      message: "El tiempo mínimo debe ser 20 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_min_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_min_aprendiz.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_max_aprendiz"
                  control={control}
                  rules={{
                    value: {
                      value: 30,
                      message: "El tiempo máximo debe ser 30 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_max_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_max_aprendiz.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <h4 className="text-center">Veterano A</h4>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_veterano_a"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_veterano_a && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_veterano_a.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_min_veterano_a"
                  control={control}
                  rules={{
                    value: {
                      value: 11,
                      message: "El tiempo mínimo debe ser 11 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_min_veterano_a && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_min_veterano_a.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_max_veterano_a"
                  control={control}
                  rules={{
                    value: {
                      value: 13,
                      message: "El tiempo máximo debe ser 13 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_max_veterano_a && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_max_veterano_a.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <h4 className="text-center">Veterano B</h4>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_veterano_b"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_veterano_b.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_min_veterano_b"
                  control={control}
                  rules={{
                    value: {
                      value: 12,
                      message: "El tiempo mínimo debe ser 12 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_min_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_min_veterano_b.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_max_veterano_b"
                  control={control}
                  rules={{
                    value: {
                      value: 18,
                      message: "El tiempo máximo debe ser 18 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_max_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_max_veterano_b.message}
                </span>
              )}
            </Form.Group>
          </Col>
        </Row>

        <hr />
        <h3>Clientes</h3>
        <Row className="align-items-start">
          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_llegada_min"
                  control={control}
                  rules={{
                    value: {
                      value: 2,
                      message: "La demora mínima debe ser 2 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_llegada_min && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_llegada_min.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_llegada_max"
                  control={control}
                  rules={{
                    value: {
                      value: 12,
                      message: "La demora máxima debe ser 12 minutos",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_llegada_max && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_llegada_max.message}
                </span>
              )}
            </Form.Group>
          </Col>
        </Row>

        <hr />
        <h3>Simulación</h3>
        <Row className="align-items-start">
          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Cantidad de días a simular
                </Form.Label>
                <Controller
                  name="cantidad_dias"
                  control={control}
                  rules={{
                    max: {
                      value: 100000,
                      message: "El valor máximo es 100000",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control type="number" {...field} placeholder="%" />
                  )}
                />
              </div>
              {errors.cantidad_dias && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.cantidad_dias.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Mostrar desde Hora
                </Form.Label>
                <Controller
                  name="hora_desde"
                  control={control}
                  rules={{
                    max: {
                      value: 8,
                      message: "El valor máximo es 8",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control type="number" {...field} placeholder="%" />
                  )}
                />
              </div>
              {errors.hora_desde && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.hora_desde.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Mostrar desde Día
                </Form.Label>
                <Controller
                  name="dia_desde"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control type="number" {...field} placeholder="%" />
                  )}
                />
              </div>
              {errors.dia_desde && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.dia_desde.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <Form.Group controlId="1" style={{ margin: "8px" }}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Cant. iteraciones
                </Form.Label>
                <Controller
                  name="cantidad_iteraciones"
                  control={control}
                  rules={{
                    max: {
                      value: 100000,
                      message: "El valor máximo es 100000",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({ field }) => (
                    <Form.Control type="number" {...field} placeholder="%" />
                  )}
                />
              </div>
              {errors.cantidad_iteraciones && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.cantidad_iteraciones.message}
                </span>
              )}
            </Form.Group>
          </Col>
        </Row>

        {errorAPI && (
          <span
            style={{
              color: "red",
              fontWeight: "600",
            }}
          >
            {errorAPI}
          </span>
        )}

        <div className="align-itms-end">
          <Button className="btn btn-primary" onClick={handleSubmit(onSubmit)}>
            Calcular
          </Button>
        </div>
      </div>
    </>
  );
};

export { DataForm };
